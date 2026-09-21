package com.repairmatch.modules.review.repository;

import com.repairmatch.modules.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    Optional<Review> findByBookingId(String bookingId);
    List<Review> findByTechnicianIdOrderByCreatedAtDesc(String technicianId);

    @Query("SELECT r FROM Review r JOIN FETCH r.customer WHERE r.technician.id = :technicianId ORDER BY r.createdAt DESC")
    List<Review> findByTechnicianIdWithCustomer(String technicianId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.technician.id = :technicianId")
    Double calculateAverageRatingByTechnicianId(String technicianId);

    int countByTechnicianId(String technicianId);
}
