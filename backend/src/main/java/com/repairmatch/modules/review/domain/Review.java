package com.repairmatch.modules.review.domain;

import com.repairmatch.modules.booking.domain.Booking;
import com.repairmatch.modules.technician.domain.TechnicianProfile;
import com.repairmatch.modules.user.domain.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reviews")
@CompoundIndexes({
    @CompoundIndex(name = "review_tech_created_idx", def = "{'technicianId': 1, 'createdAt': -1}")
})
public class Review {

    @Id
    private String id;

    @DBRef
    private Booking booking;

    @Indexed(unique = true)
    private String bookingId;

    @DBRef
    private User customer;

    @Indexed
    private String customerId;

    @DBRef
    private TechnicianProfile technician;

    @Indexed
    private String technicianId;

    private int rating; // 1 to 5
    private String comment;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Review() {}

    public Review(String id, Booking booking, User customer, TechnicianProfile technician, int rating, String comment) {
        this.id = id;
        this.booking = booking;
        if (booking != null) {
            this.bookingId = booking.getId();
        }
        this.customer = customer;
        if (customer != null) {
            this.customerId = customer.getId();
        }
        this.technician = technician;
        if (technician != null) {
            this.technicianId = technician.getId();
        }
        this.rating = rating;
        this.comment = comment;
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

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) {
        this.customer = customer;
        if (customer != null) {
            this.customerId = customer.getId();
        }
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public TechnicianProfile getTechnician() { return technician; }
    public void setTechnician(TechnicianProfile technician) {
        this.technician = technician;
        if (technician != null) {
            this.technicianId = technician.getId();
        }
    }

    public String getTechnicianId() { return technicianId; }
    public void setTechnicianId(String technicianId) { this.technicianId = technicianId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
