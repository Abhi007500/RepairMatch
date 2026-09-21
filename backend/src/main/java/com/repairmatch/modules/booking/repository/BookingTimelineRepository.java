package com.repairmatch.modules.booking.repository;

import com.repairmatch.modules.booking.domain.BookingTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingTimelineRepository extends JpaRepository<BookingTimeline, String> {
    List<BookingTimeline> findByBookingIdOrderByCreatedAtAsc(String bookingId);
}
