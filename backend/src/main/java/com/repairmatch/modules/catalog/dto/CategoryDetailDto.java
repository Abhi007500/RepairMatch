package com.repairmatch.modules.catalog.dto;

import java.util.List;

public class CategoryDetailDto {
    private CategoryDto category;
    private List<BrandDto> brands;
    private List<ProblemTypeDto> problemTypes;

    public CategoryDetailDto() {}

    public CategoryDetailDto(CategoryDto category, List<BrandDto> brands, List<ProblemTypeDto> problemTypes) {
        this.category = category;
        this.brands = brands;
        this.problemTypes = problemTypes;
    }

    public CategoryDto getCategory() { return category; }
    public void setCategory(CategoryDto category) { this.category = category; }

    public List<BrandDto> getBrands() { return brands; }
    public void setBrands(List<BrandDto> brands) { this.brands = brands; }

    public List<ProblemTypeDto> getProblemTypes() { return problemTypes; }
    public void setProblemTypes(List<ProblemTypeDto> problemTypes) { this.problemTypes = problemTypes; }
}
