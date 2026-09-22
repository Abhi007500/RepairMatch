package com.repairmatch.modules.catalog.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "problem_types")
public class ProblemType {

    @Id
    private String id;

    @DBRef
    private Category category;

    @Indexed
    private String categoryId;

    private String title;
    private String description;
    private BigDecimal typicalPriceEstimate;
    private LocalDateTime createdAt = LocalDateTime.now();

    public ProblemType() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            this.categoryId = category.getId();
        }
    }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getTypicalPriceEstimate() { return typicalPriceEstimate; }
    public void setTypicalPriceEstimate(BigDecimal typicalPriceEstimate) { this.typicalPriceEstimate = typicalPriceEstimate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
