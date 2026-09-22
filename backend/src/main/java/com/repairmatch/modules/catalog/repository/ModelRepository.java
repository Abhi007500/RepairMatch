package com.repairmatch.modules.catalog.repository;

import com.repairmatch.modules.catalog.domain.Model;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends MongoRepository<Model, String> {
    List<Model> findByCategoryIdAndBrandIdOrderByNameAsc(String categoryId, String brandId);
    List<Model> findByCategoryIdOrderByNameAsc(String categoryId);
    List<Model> findByBrandIdOrderByNameAsc(String brandId);
}
