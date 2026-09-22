package com.repairmatch.modules.technician.domain;

import com.repairmatch.modules.catalog.domain.Brand;
import com.repairmatch.modules.catalog.domain.Category;
import com.repairmatch.modules.catalog.domain.ProblemType;
import com.repairmatch.modules.user.domain.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Document(collection = "technician_profiles")
@CompoundIndexes({
    @CompoundIndex(name = "tech_status_avail_idx", def = "{'verificationStatus': 1, 'available': 1}"),
    @CompoundIndex(name = "tech_category_status_idx", def = "{'categoryIds': 1, 'verificationStatus': 1, 'available': 1}")
})
public class TechnicianProfile {

    @Id
    private String id;

    @DBRef
    private User user;

    @Indexed(unique = true)
    private String userId;

    private String bio;
    private int experienceYears;

    @Indexed
    private String verificationStatus = "PENDING"; // PENDING, VERIFIED, REJECTED

    private String kycDocumentUrl;
    private BigDecimal baseInspectionFee;
    private double serviceRadiusKm;
    private double latitude;
    private double longitude;

    @Indexed
    private double averageRating = 0.0;

    private int totalReviews = 0;
    private int completedJobsCount = 0;

    @Indexed
    private boolean available = true;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @DBRef
    private Set<Category> categories = new HashSet<>();

    @Indexed
    private Set<String> categoryIds = new HashSet<>();

    @DBRef
    private Set<Brand> brands = new HashSet<>();

    @DBRef
    private Set<ProblemType> problemTypes = new HashSet<>();

    public TechnicianProfile() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
        }
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getKycDocumentUrl() { return kycDocumentUrl; }
    public void setKycDocumentUrl(String kycDocumentUrl) { this.kycDocumentUrl = kycDocumentUrl; }

    public BigDecimal getBaseInspectionFee() { return baseInspectionFee; }
    public void setBaseInspectionFee(BigDecimal baseInspectionFee) { this.baseInspectionFee = baseInspectionFee; }

    public double getServiceRadiusKm() { return serviceRadiusKm; }
    public void setServiceRadiusKm(double serviceRadiusKm) { this.serviceRadiusKm = serviceRadiusKm; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }

    public int getCompletedJobsCount() { return completedJobsCount; }
    public void setCompletedJobsCount(int completedJobsCount) { this.completedJobsCount = completedJobsCount; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Set<Category> getCategories() { return categories; }
    public void setCategories(Set<Category> categories) {
        this.categories = categories;
        if (categories != null) {
            this.categoryIds = categories.stream().map(Category::getId).collect(Collectors.toSet());
        }
    }

    public Set<String> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(Set<String> categoryIds) { this.categoryIds = categoryIds; }

    public Set<Brand> getBrands() { return brands; }
    public void setBrands(Set<Brand> brands) { this.brands = brands; }

    public Set<ProblemType> getProblemTypes() { return problemTypes; }
    public void setProblemTypes(Set<ProblemType> problemTypes) { this.problemTypes = problemTypes; }
}
