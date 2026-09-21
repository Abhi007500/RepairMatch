package com.repairmatch.modules.booking.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateBookingRequest {

    @NotBlank(message = "Technician ID is required")
    private String technicianId;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    private String brandId;
    private String modelId;
    private String problemTypeId;

    @NotBlank(message = "Problem description is required")
    private String problemDescription;

    @NotBlank(message = "Address ID is required")
    private String addressId;

    @NotBlank(message = "Scheduled date is required")
    private String scheduledDate;

    @NotBlank(message = "Time slot is required")
    private String timeSlot;

    public CreateBookingRequest() {}

    public CreateBookingRequest(String technicianId, String categoryId, String brandId, String modelId, String problemTypeId, String problemDescription, String addressId, String scheduledDate, String timeSlot) {
        this.technicianId = technicianId;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.modelId = modelId;
        this.problemTypeId = problemTypeId;
        this.problemDescription = problemDescription;
        this.addressId = addressId;
        this.scheduledDate = scheduledDate;
        this.timeSlot = timeSlot;
    }

    public String getTechnicianId() { return technicianId; }
    public void setTechnicianId(String technicianId) { this.technicianId = technicianId; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getBrandId() { return brandId; }
    public void setBrandId(String brandId) { this.brandId = brandId; }

    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }

    public String getProblemTypeId() { return problemTypeId; }
    public void setProblemTypeId(String problemTypeId) { this.problemTypeId = problemTypeId; }

    public String getProblemDescription() { return problemDescription; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }

    public String getAddressId() { return addressId; }
    public void setAddressId(String addressId) { this.addressId = addressId; }

    public String getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(String scheduledDate) { this.scheduledDate = scheduledDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
}
