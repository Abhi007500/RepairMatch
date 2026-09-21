package com.repairmatch.modules.booking.repository;

import com.repairmatch.modules.booking.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByCustomerIdOrderByCreatedAtDesc(String customerId);
    List<Booking> findByTechnicianIdOrderByCreatedAtDesc(String technicianId);
    List<Booking> findByStatusOrderByCreatedAtDesc(String status);

    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.customer " +
           "JOIN FETCH b.technician t " +
           "JOIN FETCH t.user " +
           "JOIN FETCH b.category " +
           "LEFT JOIN FETCH b.brand " +
           "LEFT JOIN FETCH b.model " +
           "LEFT JOIN FETCH b.problemType " +
           "JOIN FETCH b.address " +
           "WHERE b.id = :id")
    Optional<Booking> findByIdWithDetails(String id);

    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.customer " +
           "JOIN FETCH b.technician t " +
           "JOIN FETCH t.user " +
           "JOIN FETCH b.category " +
           "WHERE b.customer.id = :customerId ORDER BY b.createdAt DESC")
    List<Booking> findCustomerBookingsWithDetails(String customerId);

    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.customer " +
           "JOIN FETCH b.category " +
           "JOIN FETCH b.address " +
           "WHERE b.technician.id = :technicianId ORDER BY b.createdAt DESC")
    List<Booking> findTechnicianBookingsWithDetails(String technicianId);

    boolean existsByTechnicianIdAndScheduledDateAndTimeSlotAndStatusIn(
            String technicianId, String scheduledDate, String timeSlot, List<String> statuses);
}
