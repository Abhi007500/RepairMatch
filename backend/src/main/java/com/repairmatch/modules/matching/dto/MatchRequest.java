package com.repairmatch.modules.matching.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MatchRequest {

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    private String brandId;
    private String modelId;
    private String problemTypeId;

    @NotNull(message = "Customer latitude is required")
    private Double customerLatitude;

    @NotNull(message = "Customer longitude is required")
    private Double customerLongitude;

    private String scheduledDate;
    private String timeSlot;

    public MatchRequest() {}

    public MatchRequest(String categoryId, String brandId, String modelId, String problemTypeId, Double customerLatitude, Double customerLongitude) {
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.modelId = modelId;
        this.problemTypeId = problemTypeId;
        this.customerLatitude = customerLatitude;
        this.customerLongitude = customerLongitude;
    }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getBrandId() { return brandId; }
    public void setBrandId(String brandId) { this.brandId = brandId; }

    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }

    public String getProblemTypeId() { return problemTypeId; }
    public void setProblemTypeId(String problemTypeId) { this.problemTypeId = problemTypeId; }

    public Double getCustomerLatitude() { return customerLatitude; }
    public void setCustomerLatitude(Double customerLatitude) { this.customerLatitude = customerLatitude; }

    public Double getCustomerLongitude() { return customerLongitude; }
    public void setCustomerLongitude(Double customerLongitude) { this.customerLongitude = customerLongitude; }

    public String getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(String scheduledDate) { this.scheduledDate = scheduledDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
}
