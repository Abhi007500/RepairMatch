package com.repairmatch.modules.catalog.repository;

import com.repairmatch.modules.catalog.domain.Brand;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends MongoRepository<Brand, String> {
    Optional<Brand> findBySlug(String slug);

    List<Brand> findByCategoryIdsContainingOrderByNameAsc(String categoryId);

    default List<Brand> findByCategoryId(String categoryId) {
        return findByCategoryIdsContainingOrderByNameAsc(categoryId);
    }
}
