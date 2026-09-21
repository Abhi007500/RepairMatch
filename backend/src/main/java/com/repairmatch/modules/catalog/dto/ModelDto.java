package com.repairmatch.modules.catalog.dto;

import com.repairmatch.modules.catalog.domain.Model;

public class ModelDto {
    private String id;
    private String name;
    private String brandId;
    private String brandName;
    private String categoryId;

    public ModelDto() {}

    public ModelDto(String id, String name, String brandId, String brandName, String categoryId) {
        this.id = id;
        this.name = name;
        this.brandId = brandId;
        this.brandName = brandName;
        this.categoryId = categoryId;
    }

    public static ModelDto fromEntity(Model model) {
        return new ModelDto(
                model.getId(),
                model.getName(),
                model.getBrand() != null ? model.getBrand().getId() : null,
                model.getBrand() != null ? model.getBrand().getName() : null,
                model.getCategory() != null ? model.getCategory().getId() : null
        );
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrandId() { return brandId; }
    public void setBrandId(String brandId) { this.brandId = brandId; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
}
