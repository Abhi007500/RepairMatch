package com.repairmatch.modules.payment.domain;

import com.repairmatch.modules.booking.domain.Booking;
import com.repairmatch.modules.user.domain.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "payments")
@CompoundIndexes({
    @CompoundIndex(name = "payment_booking_created_idx", def = "{'bookingId': 1, 'createdAt': -1}")
})
public class Payment {

    @Id
    private String id;

    @DBRef
    private Booking booking;

    @Indexed
    private String bookingId;

    @DBRef
    private User customer;

    @Indexed
    private String customerId;

    private BigDecimal amount;
    private String currency = "INR";
    private String paymentMethod = "RAZORPAY";

    @Indexed
    private String paymentStatus = "PENDING"; // PENDING, AUTHORIZED, PAID, FAILED, REFUNDED, CANCELLED

    private String provider = "RAZORPAY";

    @Indexed
    private String providerOrderId;

    @Indexed
    private String providerPaymentId;

    private String providerSignature;
    private String failureReason;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Payment() {}

    public Payment(
            String id,
            Booking booking,
            User customer,
            BigDecimal amount,
            String currency,
            String paymentMethod,
            String provider,
            String providerOrderId) {
        this.id = id;
        this.booking = booking;
        if (booking != null) {
            this.bookingId = booking.getId();
        }
        this.customer = customer;
        if (customer != null) {
            this.customerId = customer.getId();
        }
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.provider = provider;
        this.providerOrderId = providerOrderId;
        this.paymentStatus = "PENDING";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getProviderOrderId() { return providerOrderId; }
    public void setProviderOrderId(String providerOrderId) { this.providerOrderId = providerOrderId; }

    public String getProviderPaymentId() { return providerPaymentId; }
    public void setProviderPaymentId(String providerPaymentId) { this.providerPaymentId = providerPaymentId; }

    public String getProviderSignature() { return providerSignature; }
    public void setProviderSignature(String providerSignature) { this.providerSignature = providerSignature; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
