package com.repairmatch.modules.catalog.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "categories")
public class Category {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    @Indexed(unique = true)
    private String slug;

    private String description;
    private String iconName;
    private boolean requiresBrandAndModel;

    @Indexed
    private int displayOrder;

    private LocalDateTime createdAt = LocalDateTime.now();

    @DBRef
    private Set<Brand> brands = new HashSet<>();

    public Category() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }

    public boolean isRequiresBrandAndModel() { return requiresBrandAndModel; }
    public void setRequiresBrandAndModel(boolean requiresBrandAndModel) { this.requiresBrandAndModel = requiresBrandAndModel; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<Brand> getBrands() { return brands; }
    public void setBrands(Set<Brand> brands) { this.brands = brands; }
}
