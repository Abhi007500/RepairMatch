package com.repairmatch.modules.catalog.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "models")
public class Model {

    @Id
    private String id;

    @DBRef
    private Brand brand;

    @Indexed
    private String brandId;

    @DBRef
    private Category category;

    @Indexed
    private String categoryId;

    private String name;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Model() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) {
        this.brand = brand;
        if (brand != null) {
            this.brandId = brand.getId();
        }
    }

    public String getBrandId() { return brandId; }
    public void setBrandId(String brandId) { this.brandId = brandId; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            this.categoryId = category.getId();
        }
    }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
