package com.repairmatch.modules.review.dto;

import com.repairmatch.modules.review.domain.Review;

import java.time.LocalDateTime;

public class ReviewDto {
    private String id;
    private String bookingId;
    private String customerId;
    private String customerName;
    private String technicianId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewDto() {}

    public static ReviewDto fromEntity(Review r) {
        ReviewDto dto = new ReviewDto();
        dto.id = r.getId();
        dto.bookingId = r.getBooking() != null ? r.getBooking().getId() : null;
        dto.customerId = r.getCustomer() != null ? r.getCustomer().getId() : null;
        dto.customerName = r.getCustomer() != null ? r.getCustomer().getFullName() : null;
        dto.technicianId = r.getTechnician() != null ? r.getTechnician().getId() : null;
        dto.rating = r.getRating();
        dto.comment = r.getComment();
        dto.createdAt = r.getCreatedAt();
        return dto;
    }

    public String getId() { return id; }
    public String getBookingId() { return bookingId; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getTechnicianId() { return technicianId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
