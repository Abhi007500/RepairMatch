package com.repairmatch.modules.matching.dto;

import java.math.BigDecimal;
import java.util.List;

public class TechnicianMatchDto {

    private String technicianId;
    private String name;
    private String bio;
    private int suitabilityScore; // 0 - 100
    private double distanceKm;
    private BigDecimal baseInspectionFee;
    private double averageRating;
    private int totalReviews;
    private int completedJobsCount;
    private int experienceYears;
    private List<String> matchHighlights;
    private boolean brandMatched;
    private boolean problemMatched;

    public TechnicianMatchDto() {}

    public String getTechnicianId() { return technicianId; }
    public void setTechnicianId(String technicianId) { this.technicianId = technicianId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public int getSuitabilityScore() { return suitabilityScore; }
    public void setSuitabilityScore(int suitabilityScore) { this.suitabilityScore = suitabilityScore; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public BigDecimal getBaseInspectionFee() { return baseInspectionFee; }
    public void setBaseInspectionFee(BigDecimal baseInspectionFee) { this.baseInspectionFee = baseInspectionFee; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }

    public int getCompletedJobsCount() { return completedJobsCount; }
    public void setCompletedJobsCount(int completedJobsCount) { this.completedJobsCount = completedJobsCount; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    public List<String> getMatchHighlights() { return matchHighlights; }
    public void setMatchHighlights(List<String> matchHighlights) { this.matchHighlights = matchHighlights; }

    public boolean isBrandMatched() { return brandMatched; }
    public void setBrandMatched(boolean brandMatched) { this.brandMatched = brandMatched; }

    public boolean isProblemMatched() { return problemMatched; }
    public void setProblemMatched(boolean problemMatched) { this.problemMatched = problemMatched; }
}
