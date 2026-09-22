package com.repairmatch.modules.booking.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "booking_timeline")
public class BookingTimeline {

    @Id
    private String id;

    @DBRef
    private Booking booking;

    @Indexed
    private String bookingId;

    private String status;
    private String remarks;
    private LocalDateTime createdAt = LocalDateTime.now();

    public BookingTimeline() {}

    public BookingTimeline(String id, Booking booking, String status, String remarks) {
        this.id = id;
        this.booking = booking;
        if (booking != null) {
            this.bookingId = booking.getId();
        }
        this.status = status;
        this.remarks = remarks;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) {
        this.booking = booking;
        if (booking != null) {
            this.bookingId = booking.getId();
        }
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
