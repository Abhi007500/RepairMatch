package com.repairmatch.modules.matching.service;

import com.repairmatch.common.exception.ResourceNotFoundException;
import com.repairmatch.common.util.GeoUtils;
import com.repairmatch.modules.booking.repository.BookingRepository;
import com.repairmatch.modules.catalog.domain.Category;
import com.repairmatch.modules.catalog.repository.CategoryRepository;
import com.repairmatch.modules.matching.dto.MatchRequest;
import com.repairmatch.modules.matching.dto.TechnicianMatchDto;
import com.repairmatch.modules.technician.domain.TechnicianProfile;
import com.repairmatch.modules.technician.repository.TechnicianProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MatchingEngineService {

    private final TechnicianProfileRepository technicianProfileRepository;
    private final CategoryRepository categoryRepository;
    private final BookingRepository bookingRepository;

    public MatchingEngineService(
            TechnicianProfileRepository technicianProfileRepository,
            CategoryRepository categoryRepository,
            BookingRepository bookingRepository) {
        this.technicianProfileRepository = technicianProfileRepository;
        this.categoryRepository = categoryRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public List<TechnicianMatchDto> findMatches(MatchRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        // STAGE 1: HARD FILTERING
        List<TechnicianProfile> candidates = technicianProfileRepository
                .findVerifiedAndAvailableByCategoryId(request.getCategoryId());

        List<TechnicianMatchDto> matchedResults = new ArrayList<>();

        for (TechnicianProfile tech : candidates) {
            // Hard Filter: Distance
            double distanceKm = GeoUtils.calculateDistanceKm(
                    request.getCustomerLatitude(),
                    request.getCustomerLongitude(),
                    tech.getLatitude(),
                    tech.getLongitude()
            );

            if (distanceKm > tech.getServiceRadiusKm()) {
                continue; // Outside technician's operating radius
            }

            // Hard Filter: Slot conflict check
            if (request.getScheduledDate() != null && request.getTimeSlot() != null) {
                boolean hasConflict = bookingRepository.existsByTechnicianIdAndScheduledDateAndTimeSlotAndStatusIn(
                        tech.getId(),
                        request.getScheduledDate(),
                        request.getTimeSlot(),
                        List.of("PENDING", "ACCEPTED", "IN_PROGRESS")
                );
                if (hasConflict) {
                    continue; // Technician already booked for this slot
                }
            }

            // STAGE 2: MULTI-FACTOR SCORING
            boolean brandMatched = false;
            double brandScore = 100.0;
            if (category.isRequiresBrandAndModel() && request.getBrandId() != null) {
                brandMatched = tech.getBrands().stream()
                        .anyMatch(b -> b.getId().equals(request.getBrandId()));
                brandScore = brandMatched ? 100.0 : 25.0;
            }

            boolean problemMatched = false;
            double problemScore = 70.0;
            if (request.getProblemTypeId() != null) {
                problemMatched = tech.getProblemTypes().stream()
                        .anyMatch(p -> p.getId().equals(request.getProblemTypeId()));
                problemScore = problemMatched ? 100.0 : 40.0;
            }

            // Distance score (0 to 100, linear decrease with distance within radius)
            double distanceRatio = Math.min(1.0, distanceKm / Math.max(1.0, tech.getServiceRadiusKm()));
            double distanceScore = Math.max(0.0, 100.0 * (1.0 - (0.8 * distanceRatio)));
            if (distanceKm <= 2.5) {
                distanceScore = 100.0;
            }

            // Rating score
            double ratingScore;
            if (tech.getTotalReviews() == 0 || tech.getAverageRating() == 0.0) {
                ratingScore = 65.0; // Baseline for new verified technicians
            } else {
                ratingScore = Math.min(100.0, (tech.getAverageRating() / 5.0) * 100.0);
            }

            // Service history score
            double historyScore;
            int jobs = tech.getCompletedJobsCount();
            if (jobs >= 100) historyScore = 100.0;
            else if (jobs >= 50) historyScore = 85.0;
            else if (jobs >= 20) historyScore = 75.0;
            else if (jobs >= 5) historyScore = 60.0;
            else historyScore = 45.0;

            // Price competitiveness score (benchmark: 250.00)
            double priceScore = 75.0;
            if (tech.getBaseInspectionFee() != null) {
                double fee = tech.getBaseInspectionFee().doubleValue();
                if (fee <= 199.00) priceScore = 100.0;
                else if (fee <= 250.00) priceScore = 85.0;
                else if (fee <= 350.00) priceScore = 70.0;
                else priceScore = 50.0;
            }

            // Weighted aggregation
            // W1 = 0.25 (Brand), W2 = 0.20 (Problem), W3 = 0.20 (Distance),
            // W4 = 0.15 (Rating), W5 = 0.10 (History), W6 = 0.10 (Price)
            double totalScore = (brandScore * 0.25)
                    + (problemScore * 0.20)
                    + (distanceScore * 0.20)
                    + (ratingScore * 0.15)
                    + (historyScore * 0.10)
                    + (priceScore * 0.10);

            int finalSuitabilityScore = (int) Math.round(Math.min(100.0, Math.max(1.0, totalScore)));

            // Highlights generation
            List<String> highlights = new ArrayList<>();
            if (brandMatched) {
                highlights.add("Brand Specialist");
            }
            if (problemMatched) {
                highlights.add("Expert in this issue");
            }
            if (distanceKm <= 3.0) {
                highlights.add(String.format("Very close (%s km)", distanceKm));
            } else if (distanceKm <= 7.0) {
                highlights.add(String.format("Within %s km", distanceKm));
            }
            if (tech.getAverageRating() >= 4.7 && tech.getTotalReviews() >= 10) {
                highlights.add(String.format("Top Rated (%.1f ★)", tech.getAverageRating()));
            }
            if (tech.getCompletedJobsCount() >= 50) {
                highlights.add(String.format("%d+ jobs completed", tech.getCompletedJobsCount()));
            }
            if (tech.getBaseInspectionFee() != null && tech.getBaseInspectionFee().compareTo(new BigDecimal("200.00")) <= 0) {
                highlights.add("Great Value Fee");
            }

            TechnicianMatchDto dto = new TechnicianMatchDto();
            dto.setTechnicianId(tech.getId());
            dto.setName(tech.getUser().getFullName());
            dto.setBio(tech.getBio());
            dto.setSuitabilityScore(finalSuitabilityScore);
            dto.setDistanceKm(distanceKm);
            dto.setBaseInspectionFee(tech.getBaseInspectionFee());
            dto.setAverageRating(tech.getAverageRating());
            dto.setTotalReviews(tech.getTotalReviews());
            dto.setCompletedJobsCount(tech.getCompletedJobsCount());
            dto.setExperienceYears(tech.getExperienceYears());
            dto.setMatchHighlights(highlights);
            dto.setBrandMatched(brandMatched);
            dto.setProblemMatched(problemMatched);

            matchedResults.add(dto);
        }

        // Sort descending by suitability score
        matchedResults.sort(Comparator.comparingInt(TechnicianMatchDto::getSuitabilityScore).reversed());

        return matchedResults;
    }
}
