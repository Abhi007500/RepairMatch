package com.repairmatch.modules.payment.dto;

import jakarta.validation.constraints.NotBlank;

public class CreatePaymentOrderRequest {

    @NotBlank(message = "Booking ID is required")
    private String bookingId;

    public CreatePaymentOrderRequest() {}

    public CreatePaymentOrderRequest(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
}
