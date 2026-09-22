package com.repairmatch.modules.booking.repository;

import com.repairmatch.modules.booking.domain.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByCustomerIdOrderByCreatedAtDesc(String customerId);
    List<Booking> findByTechnicianIdOrderByCreatedAtDesc(String technicianId);
    List<Booking> findByStatusOrderByCreatedAtDesc(String status);

    default Optional<Booking> findByIdWithDetails(String id) {
        return findById(id);
    }

    default List<Booking> findCustomerBookingsWithDetails(String customerId) {
        return findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    default List<Booking> findTechnicianBookingsWithDetails(String technicianId) {
        return findByTechnicianIdOrderByCreatedAtDesc(technicianId);
    }

    boolean existsByTechnicianIdAndScheduledDateAndTimeSlotAndStatusIn(
            String technicianId, String scheduledDate, String timeSlot, List<String> statuses);
}
