package com.repairmatch.modules.technician.domain;

import com.repairmatch.modules.catalog.domain.Brand;
import com.repairmatch.modules.catalog.domain.Category;
import com.repairmatch.modules.catalog.domain.ProblemType;
import com.repairmatch.modules.user.domain.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "technician_profiles")
public class TechnicianProfile {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "bio", length = 1000)
    private String bio;

    @Column(name = "experience_years", nullable = false)
    private int experienceYears;

    @Column(name = "verification_status", length = 20, nullable = false)
    private String verificationStatus; // PENDING, VERIFIED, REJECTED

    @Column(name = "kyc_document_url", length = 255)
    private String kycDocumentUrl;

    @Column(name = "base_inspection_fee", nullable = false)
    private BigDecimal baseInspectionFee;

    @Column(name = "service_radius_km", nullable = false)
    private double serviceRadiusKm;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "average_rating", nullable = false)
    private double averageRating = 0.0;

    @Column(name = "total_reviews", nullable = false)
    private int totalReviews = 0;

    @Column(name = "completed_jobs_count", nullable = false)
    private int completedJobsCount = 0;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
        name = "technician_categories",
        joinColumns = @JoinColumn(name = "technician_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "technician_brands",
        joinColumns = @JoinColumn(name = "technician_id"),
        inverseJoinColumns = @JoinColumn(name = "brand_id")
    )
    private Set<Brand> brands = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "technician_problems",
        joinColumns = @JoinColumn(name = "technician_id"),
        inverseJoinColumns = @JoinColumn(name = "problem_type_id")
    )
    private Set<ProblemType> problemTypes = new HashSet<>();

    public TechnicianProfile() {}

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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
    public void setCategories(Set<Category> categories) { this.categories = categories; }

    public Set<Brand> getBrands() { return brands; }
    public void setBrands(Set<Brand> brands) { this.brands = brands; }

    public Set<ProblemType> getProblemTypes() { return problemTypes; }
    public void setProblemTypes(Set<ProblemType> problemTypes) { this.problemTypes = problemTypes; }
}
