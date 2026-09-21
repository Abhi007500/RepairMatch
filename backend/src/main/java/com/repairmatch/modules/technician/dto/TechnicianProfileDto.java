package com.repairmatch.modules.technician.dto;

import com.repairmatch.modules.catalog.dto.BrandDto;
import com.repairmatch.modules.catalog.dto.CategoryDto;
import com.repairmatch.modules.catalog.dto.ProblemTypeDto;
import com.repairmatch.modules.technician.domain.TechnicianProfile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class TechnicianProfileDto {
    private String id;
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String bio;
    private int experienceYears;
    private String verificationStatus;
    private String kycDocumentUrl;
    private BigDecimal baseInspectionFee;
    private double serviceRadiusKm;
    private double latitude;
    private double longitude;
    private double averageRating;
    private int totalReviews;
    private int completedJobsCount;
    private boolean available;
    private List<CategoryDto> categories;
    private List<BrandDto> brands;
    private List<ProblemTypeDto> problemTypes;

    public TechnicianProfileDto() {}

    public static TechnicianProfileDto fromEntity(TechnicianProfile t) {
        TechnicianProfileDto dto = new TechnicianProfileDto();
        dto.id = t.getId();
        dto.userId = t.getUser() != null ? t.getUser().getId() : null;
        dto.name = t.getUser() != null ? t.getUser().getFullName() : null;
        dto.email = t.getUser() != null ? t.getUser().getEmail() : null;
        dto.phone = t.getUser() != null ? t.getUser().getPhoneNumber() : null;
        dto.bio = t.getBio();
        dto.experienceYears = t.getExperienceYears();
        dto.verificationStatus = t.getVerificationStatus();
        dto.kycDocumentUrl = t.getKycDocumentUrl();
        dto.baseInspectionFee = t.getBaseInspectionFee();
        dto.serviceRadiusKm = t.getServiceRadiusKm();
        dto.latitude = t.getLatitude();
        dto.longitude = t.getLongitude();
        dto.averageRating = t.getAverageRating();
        dto.totalReviews = t.getTotalReviews();
        dto.completedJobsCount = t.getCompletedJobsCount();
        dto.available = t.isAvailable();

        dto.categories = t.getCategories().stream().map(CategoryDto::fromEntity).collect(Collectors.toList());
        dto.brands = t.getBrands().stream().map(BrandDto::fromEntity).collect(Collectors.toList());
        dto.problemTypes = t.getProblemTypes().stream().map(ProblemTypeDto::fromEntity).collect(Collectors.toList());

        return dto;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getBio() { return bio; }
    public int getExperienceYears() { return experienceYears; }
    public String getVerificationStatus() { return verificationStatus; }
    public String getKycDocumentUrl() { return kycDocumentUrl; }
    public BigDecimal getBaseInspectionFee() { return baseInspectionFee; }
    public double getServiceRadiusKm() { return serviceRadiusKm; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getAverageRating() { return averageRating; }
    public int getTotalReviews() { return totalReviews; }
    public int getCompletedJobsCount() { return completedJobsCount; }
    public boolean isAvailable() { return available; }
    public List<CategoryDto> getCategories() { return categories; }
    public List<BrandDto> getBrands() { return brands; }
    public List<ProblemTypeDto> getProblemTypes() { return problemTypes; }
}
