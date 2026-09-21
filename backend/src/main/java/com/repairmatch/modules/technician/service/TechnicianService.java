package com.repairmatch.modules.technician.service;

import com.repairmatch.common.exception.ResourceNotFoundException;
import com.repairmatch.modules.catalog.domain.Brand;
import com.repairmatch.modules.catalog.domain.Category;
import com.repairmatch.modules.catalog.domain.ProblemType;
import com.repairmatch.modules.catalog.repository.BrandRepository;
import com.repairmatch.modules.catalog.repository.CategoryRepository;
import com.repairmatch.modules.catalog.repository.ProblemTypeRepository;
import com.repairmatch.modules.technician.domain.TechnicianProfile;
import com.repairmatch.modules.technician.dto.TechnicianProfileDto;
import com.repairmatch.modules.technician.dto.UpdateTechnicianProfileRequest;
import com.repairmatch.modules.technician.repository.TechnicianProfileRepository;
import com.repairmatch.modules.user.domain.User;
import com.repairmatch.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TechnicianService {

    private final TechnicianProfileRepository technicianProfileRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProblemTypeRepository problemTypeRepository;

    public TechnicianService(
            TechnicianProfileRepository technicianProfileRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            ProblemTypeRepository problemTypeRepository) {
        this.technicianProfileRepository = technicianProfileRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.problemTypeRepository = problemTypeRepository;
    }

    @Transactional(readOnly = true)
    public TechnicianProfileDto getMyProfile(String email) {
        User user = getUserByEmail(email);
        TechnicianProfile profile = technicianProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Technician profile not found for user: " + email));
        return TechnicianProfileDto.fromEntity(profile);
    }

    @Transactional(readOnly = true)
    public TechnicianProfileDto getPublicProfile(String technicianId) {
        TechnicianProfile profile = technicianProfileRepository.findByIdWithDetails(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + technicianId));
        return TechnicianProfileDto.fromEntity(profile);
    }

    @Transactional
    public TechnicianProfileDto updateMyProfile(String email, UpdateTechnicianProfileRequest request) {
        User user = getUserByEmail(email);
        TechnicianProfile profile = technicianProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Technician profile not found for user: " + email));

        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getExperienceYears() != null) profile.setExperienceYears(request.getExperienceYears());
        if (request.getBaseInspectionFee() != null) profile.setBaseInspectionFee(request.getBaseInspectionFee());
        if (request.getServiceRadiusKm() != null) profile.setServiceRadiusKm(request.getServiceRadiusKm());
        if (request.getLatitude() != null) profile.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) profile.setLongitude(request.getLongitude());
        if (request.getAvailable() != null) profile.setAvailable(request.getAvailable());

        if (request.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            profile.setCategories(new HashSet<>(categories));
        }

        if (request.getBrandIds() != null) {
            List<Brand> brands = brandRepository.findAllById(request.getBrandIds());
            profile.setBrands(new HashSet<>(brands));
        }

        if (request.getProblemTypeIds() != null) {
            List<ProblemType> problems = problemTypeRepository.findAllById(request.getProblemTypeIds());
            profile.setProblemTypes(new HashSet<>(problems));
        }

        TechnicianProfile saved = technicianProfileRepository.save(profile);
        return TechnicianProfileDto.fromEntity(saved);
    }

    @Transactional
    public void setAvailability(String email, boolean available) {
        User user = getUserByEmail(email);
        TechnicianProfile profile = technicianProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Technician profile not found for user: " + email));
        profile.setAvailable(available);
        technicianProfileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public List<TechnicianProfileDto> getPendingTechnicians() {
        return technicianProfileRepository.findByVerificationStatus("PENDING").stream()
                .map(TechnicianProfileDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TechnicianProfileDto updateVerificationStatus(String technicianId, String status) {
        TechnicianProfile profile = technicianProfileRepository.findById(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + technicianId));

        profile.setVerificationStatus(status.toUpperCase());
        TechnicianProfile saved = technicianProfileRepository.save(profile);
        return TechnicianProfileDto.fromEntity(saved);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
