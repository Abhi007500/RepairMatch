package com.repairmatch.modules.technician.dto;

import java.math.BigDecimal;
import java.util.List;

public class UpdateTechnicianProfileRequest {
    private String bio;
    private Integer experienceYears;
    private BigDecimal baseInspectionFee;
    private Double serviceRadiusKm;
    private Double latitude;
    private Double longitude;
    private Boolean available;
    private List<String> categoryIds;
    private List<String> brandIds;
    private List<String> problemTypeIds;

    public UpdateTechnicianProfileRequest() {}

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public BigDecimal getBaseInspectionFee() { return baseInspectionFee; }
    public void setBaseInspectionFee(BigDecimal baseInspectionFee) { this.baseInspectionFee = baseInspectionFee; }

    public Double getServiceRadiusKm() { return serviceRadiusKm; }
    public void setServiceRadiusKm(Double serviceRadiusKm) { this.serviceRadiusKm = serviceRadiusKm; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public List<String> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(List<String> categoryIds) { this.categoryIds = categoryIds; }

    public List<String> getBrandIds() { return brandIds; }
    public void setBrandIds(List<String> brandIds) { this.brandIds = brandIds; }

    public List<String> getProblemTypeIds() { return problemTypeIds; }
    public void setProblemTypeIds(List<String> problemTypeIds) { this.problemTypeIds = problemTypeIds; }
}
