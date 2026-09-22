package com.repairmatch.modules.review.repository;

import com.repairmatch.modules.review.domain.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    Optional<Review> findByBookingId(String bookingId);
    List<Review> findByTechnicianIdOrderByCreatedAtDesc(String technicianId);

    default List<Review> findByTechnicianIdWithCustomer(String technicianId) {
        return findByTechnicianIdOrderByCreatedAtDesc(technicianId);
    }

    int countByTechnicianId(String technicianId);

    default Double calculateAverageRatingByTechnicianId(String technicianId) {
        List<Review> list = findByTechnicianIdOrderByCreatedAtDesc(technicianId);
        if (list.isEmpty()) {
            return null;
        }
        return list.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }
}
