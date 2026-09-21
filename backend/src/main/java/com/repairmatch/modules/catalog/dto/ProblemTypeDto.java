package com.repairmatch.modules.catalog.dto;

import com.repairmatch.modules.catalog.domain.ProblemType;

import java.math.BigDecimal;

public class ProblemTypeDto {
    private String id;
    private String categoryId;
    private String title;
    private String description;
    private BigDecimal typicalPriceEstimate;

    public ProblemTypeDto() {}

    public ProblemTypeDto(String id, String categoryId, String title, String description, BigDecimal typicalPriceEstimate) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.typicalPriceEstimate = typicalPriceEstimate;
    }

    public static ProblemTypeDto fromEntity(ProblemType problemType) {
        return new ProblemTypeDto(
                problemType.getId(),
                problemType.getCategory() != null ? problemType.getCategory().getId() : null,
                problemType.getTitle(),
                problemType.getDescription(),
                problemType.getTypicalPriceEstimate()
        );
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getTypicalPriceEstimate() { return typicalPriceEstimate; }
    public void setTypicalPriceEstimate(BigDecimal typicalPriceEstimate) { this.typicalPriceEstimate = typicalPriceEstimate; }
}
