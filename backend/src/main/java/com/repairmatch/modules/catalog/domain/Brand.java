package com.repairmatch.modules.catalog.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "brands")
public class Brand {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    @Indexed(unique = true)
    private String slug;

    @Indexed
    private Set<String> categoryIds = new HashSet<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    public Brand() {}

    public Brand(String id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Set<String> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(Set<String> categoryIds) { this.categoryIds = categoryIds; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
