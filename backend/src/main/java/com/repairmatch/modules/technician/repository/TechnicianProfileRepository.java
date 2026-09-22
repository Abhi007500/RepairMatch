package com.repairmatch.modules.technician.repository;

import com.repairmatch.modules.technician.domain.TechnicianProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianProfileRepository extends MongoRepository<TechnicianProfile, String> {
    Optional<TechnicianProfile> findByUserId(String userId);
    List<TechnicianProfile> findByVerificationStatus(String verificationStatus);

    List<TechnicianProfile> findByVerificationStatusAndAvailableTrueAndCategoryIdsContaining(String status, String categoryId);

    default List<TechnicianProfile> findVerifiedAndAvailableByCategoryId(String categoryId) {
        return findByVerificationStatusAndAvailableTrueAndCategoryIdsContaining("VERIFIED", categoryId);
    }

    default Optional<TechnicianProfile> findByIdWithDetails(String id) {
        return findById(id);
    }
}
