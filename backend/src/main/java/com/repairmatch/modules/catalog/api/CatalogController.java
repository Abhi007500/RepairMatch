package com.repairmatch.modules.catalog.api;

import com.repairmatch.modules.catalog.dto.*;
import com.repairmatch.modules.catalog.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(catalogService.getAllCategories());
    }

    @GetMapping("/categories/{idOrSlug}")
    public ResponseEntity<CategoryDetailDto> getCategoryDetail(@PathVariable String idOrSlug) {
        return ResponseEntity.ok(catalogService.getCategoryDetail(idOrSlug));
    }

    @GetMapping("/categories/{categoryId}/brands")
    public ResponseEntity<List<BrandDto>> getBrandsByCategory(@PathVariable String categoryId) {
        return ResponseEntity.ok(catalogService.getBrandsByCategory(categoryId));
    }

    @GetMapping("/brands/{brandId}/models")
    public ResponseEntity<List<ModelDto>> getModelsByBrand(@PathVariable String brandId) {
        return ResponseEntity.ok(catalogService.getModelsByBrand(brandId));
    }

    @GetMapping("/categories/{categoryId}/brands/{brandId}/models")
    public ResponseEntity<List<ModelDto>> getModelsByBrandAndCategory(
            @PathVariable String categoryId,
            @PathVariable String brandId) {
        return ResponseEntity.ok(catalogService.getModelsByBrandAndCategory(brandId, categoryId));
    }

    @GetMapping("/categories/{categoryId}/problems")
    public ResponseEntity<List<ProblemTypeDto>> getProblemsByCategory(@PathVariable String categoryId) {
        return ResponseEntity.ok(catalogService.getProblemsByCategory(categoryId));
    }
}
