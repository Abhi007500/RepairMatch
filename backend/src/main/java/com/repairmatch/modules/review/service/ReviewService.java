package com.repairmatch.modules.review.service;

import com.repairmatch.common.exception.BadRequestException;
import com.repairmatch.common.exception.ResourceNotFoundException;
import com.repairmatch.modules.booking.domain.Booking;
import com.repairmatch.modules.booking.repository.BookingRepository;
import com.repairmatch.modules.review.domain.Review;
import com.repairmatch.modules.review.dto.CreateReviewRequest;
import com.repairmatch.modules.review.dto.ReviewDto;
import com.repairmatch.modules.review.repository.ReviewRepository;
import com.repairmatch.modules.technician.domain.TechnicianProfile;
import com.repairmatch.modules.technician.repository.TechnicianProfileRepository;
import com.repairmatch.modules.user.domain.User;
import com.repairmatch.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TechnicianProfileRepository technicianProfileRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            BookingRepository bookingRepository,
            UserRepository userRepository,
            TechnicianProfileRepository technicianProfileRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.technicianProfileRepository = technicianProfileRepository;
    }

    @Transactional
    public ReviewDto createReview(String customerEmail, CreateReviewRequest request) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + customerEmail));

        Booking booking = bookingRepository.findByIdWithDetails(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.getBookingId()));

        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException("You can only review bookings you have created.");
        }

        if (!"COMPLETED".equalsIgnoreCase(booking.getStatus())) {
            throw new BadRequestException("Reviews can only be submitted for completed repair services.");
        }

        if (reviewRepository.findByBookingId(booking.getId()).isPresent()) {
            throw new BadRequestException("A review has already been submitted for this service booking.");
        }

        TechnicianProfile technician = booking.getTechnician();

        Review review = new Review();
        review.setId(UUID.randomUUID().toString());
        review.setBooking(booking);
        review.setCustomer(customer);
        review.setTechnician(technician);
        review.setRating(request.getRating());
        review.setComment(request.getComment().trim());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        // Recalculate technician rating & total reviews
        Double avgRating = reviewRepository.calculateAverageRatingByTechnicianId(technician.getId());
        int totalReviews = reviewRepository.countByTechnicianId(technician.getId());

        if (avgRating != null) {
            double roundedAvg = Math.round(avgRating * 10.0) / 10.0;
            technician.setAverageRating(roundedAvg);
            technician.setTotalReviews(totalReviews);
            technicianProfileRepository.save(technician);
        }

        return ReviewDto.fromEntity(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getTechnicianReviews(String technicianId) {
        return reviewRepository.findByTechnicianIdWithCustomer(technicianId).stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }
}
