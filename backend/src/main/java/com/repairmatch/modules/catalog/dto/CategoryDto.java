package com.repairmatch.modules.catalog.dto;

import com.repairmatch.modules.catalog.domain.Category;

public class CategoryDto {
    private String id;
    private String name;
    private String slug;
    private String description;
    private String iconName;
    private boolean requiresBrandAndModel;
    private int displayOrder;

    public CategoryDto() {}

    public CategoryDto(String id, String name, String slug, String description, String iconName, boolean requiresBrandAndModel, int displayOrder) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.iconName = iconName;
        this.requiresBrandAndModel = requiresBrandAndModel;
        this.displayOrder = displayOrder;
    }

    public static CategoryDto fromEntity(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getIconName(),
                category.isRequiresBrandAndModel(),
                category.getDisplayOrder()
        );
    }

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
}
