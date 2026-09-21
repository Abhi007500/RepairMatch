package com.repairmatch.modules.technician.repository;

import com.repairmatch.modules.technician.domain.TechnicianProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianProfileRepository extends JpaRepository<TechnicianProfile, String> {
    Optional<TechnicianProfile> findByUserId(String userId);
    List<TechnicianProfile> findByVerificationStatus(String verificationStatus);

    @Query("SELECT DISTINCT t FROM TechnicianProfile t " +
           "JOIN t.categories c " +
           "LEFT JOIN FETCH t.categories " +
           "LEFT JOIN FETCH t.brands " +
           "LEFT JOIN FETCH t.problemTypes " +
           "JOIN FETCH t.user " +
           "WHERE t.verificationStatus = 'VERIFIED' " +
           "AND t.available = true " +
           "AND c.id = :categoryId")
    List<TechnicianProfile> findVerifiedAndAvailableByCategoryId(String categoryId);

    @Query("SELECT DISTINCT t FROM TechnicianProfile t " +
           "LEFT JOIN FETCH t.categories " +
           "LEFT JOIN FETCH t.brands " +
           "LEFT JOIN FETCH t.problemTypes " +
           "JOIN FETCH t.user " +
           "WHERE t.id = :id")
    Optional<TechnicianProfile> findByIdWithDetails(String id);
}
