package com.repairmatch.modules.payment.dto;

import java.math.BigDecimal;

public class PaymentOrderResponse {
    private String paymentId;
    private String bookingId;
    private String bookingReference;
    private BigDecimal amount;
    private String currency;
    private String provider;
    private String providerOrderId;
    private String razorpayKeyId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    public PaymentOrderResponse() {}

    public PaymentOrderResponse(
            String paymentId,
            String bookingId,
            String bookingReference,
            BigDecimal amount,
            String currency,
            String provider,
            String providerOrderId,
            String razorpayKeyId,
            String customerName,
            String customerEmail,
            String customerPhone) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.amount = amount;
        this.currency = currency;
        this.provider = provider;
        this.providerOrderId = providerOrderId;
        this.razorpayKeyId = razorpayKeyId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getProviderOrderId() { return providerOrderId; }
    public void setProviderOrderId(String providerOrderId) { this.providerOrderId = providerOrderId; }

    public String getRazorpayKeyId() { return razorpayKeyId; }
    public void setRazorpayKeyId(String razorpayKeyId) { this.razorpayKeyId = razorpayKeyId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
}
