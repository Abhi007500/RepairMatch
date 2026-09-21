package com.repairmatch.modules.catalog.service;

import com.repairmatch.common.exception.ResourceNotFoundException;
import com.repairmatch.modules.catalog.domain.Brand;
import com.repairmatch.modules.catalog.domain.Category;
import com.repairmatch.modules.catalog.domain.Model;
import com.repairmatch.modules.catalog.domain.ProblemType;
import com.repairmatch.modules.catalog.dto.*;
import com.repairmatch.modules.catalog.repository.BrandRepository;
import com.repairmatch.modules.catalog.repository.CategoryRepository;
import com.repairmatch.modules.catalog.repository.ModelRepository;
import com.repairmatch.modules.catalog.repository.ProblemTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final ProblemTypeRepository problemTypeRepository;

    public CatalogService(
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            ModelRepository modelRepository,
            ProblemTypeRepository problemTypeRepository) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.problemTypeRepository = problemTypeRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDetailDto getCategoryDetail(String identifier) {
        Category category = findCategoryByIdOrSlug(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with identifier: " + identifier));

        List<BrandDto> brands = brandRepository.findByCategoryId(category.getId()).stream()
                .map(BrandDto::fromEntity)
                .collect(Collectors.toList());

        List<ProblemTypeDto> problems = problemTypeRepository.findByCategoryIdOrderByTitleAsc(category.getId()).stream()
                .map(ProblemTypeDto::fromEntity)
                .collect(Collectors.toList());

        return new CategoryDetailDto(CategoryDto.fromEntity(category), brands, problems);
    }

    @Transactional(readOnly = true)
    public List<BrandDto> getBrandsByCategory(String categoryId) {
        return brandRepository.findByCategoryId(categoryId).stream()
                .map(BrandDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ModelDto> getModelsByBrand(String brandId) {
        return modelRepository.findByBrandIdOrderByNameAsc(brandId).stream()
                .map(ModelDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ModelDto> getModelsByBrandAndCategory(String brandId, String categoryId) {
        return modelRepository.findByCategoryIdAndBrandIdOrderByNameAsc(categoryId, brandId).stream()
                .map(ModelDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProblemTypeDto> getProblemsByCategory(String categoryId) {
        return problemTypeRepository.findByCategoryIdOrderByTitleAsc(categoryId).stream()
                .map(ProblemTypeDto::fromEntity)
                .collect(Collectors.toList());
    }

    private Optional<Category> findCategoryByIdOrSlug(String identifier) {
        Optional<Category> byId = categoryRepository.findById(identifier);
        if (byId.isPresent()) return byId;
        return categoryRepository.findBySlug(identifier);
    }
}
