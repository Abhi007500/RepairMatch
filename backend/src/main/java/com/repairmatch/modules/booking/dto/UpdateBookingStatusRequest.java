package com.repairmatch.modules.booking.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class UpdateBookingStatusRequest {

    @NotBlank(message = "Target status is required")
    private String status;

    private String remarks;

    private BigDecimal finalAmount;

    public UpdateBookingStatusRequest() {}

    public UpdateBookingStatusRequest(String status, String remarks) {
        this.status = status;
        this.remarks = remarks;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
}
