package com.repairmatch.modules.payment.dto;

import jakarta.validation.constraints.NotBlank;

public class FailPaymentRequest {

    @NotBlank(message = "Payment ID is required")
    private String paymentId;

    private String reason;
    private String errorCode;

    public FailPaymentRequest() {}

    public FailPaymentRequest(String paymentId, String reason, String errorCode) {
        this.paymentId = paymentId;
        this.reason = reason;
        this.errorCode = errorCode;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
}
