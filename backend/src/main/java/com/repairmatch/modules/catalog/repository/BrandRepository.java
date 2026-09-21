package com.repairmatch.modules.catalog.repository;

import com.repairmatch.modules.catalog.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {
    Optional<Brand> findBySlug(String slug);

    @Query(value = "SELECT b.* FROM brands b JOIN category_brands cb ON b.id = cb.brand_id WHERE cb.category_id = :categoryId ORDER BY b.name ASC", nativeQuery = true)
    List<Brand> findByCategoryId(String categoryId);
}
