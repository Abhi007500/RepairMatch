package com.repairmatch.modules.catalog.dto;

import com.repairmatch.modules.catalog.domain.Brand;

public class BrandDto {
    private String id;
    private String name;
    private String slug;

    public BrandDto() {}

    public BrandDto(String id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }

    public static BrandDto fromEntity(Brand brand) {
        return new BrandDto(brand.getId(), brand.getName(), brand.getSlug());
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
}
