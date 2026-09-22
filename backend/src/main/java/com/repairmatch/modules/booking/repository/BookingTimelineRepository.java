package com.repairmatch.modules.booking.repository;

import com.repairmatch.modules.booking.domain.BookingTimeline;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingTimelineRepository extends MongoRepository<BookingTimeline, String> {
    List<BookingTimeline> findByBookingIdOrderByCreatedAtAsc(String bookingId);
}
