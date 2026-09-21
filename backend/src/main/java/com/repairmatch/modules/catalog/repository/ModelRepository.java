package com.repairmatch.modules.catalog.repository;

import com.repairmatch.modules.catalog.domain.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, String> {
    List<Model> findByCategoryIdAndBrandIdOrderByNameAsc(String categoryId, String brandId);
    List<Model> findByCategoryIdOrderByNameAsc(String categoryId);
    List<Model> findByBrandIdOrderByNameAsc(String brandId);
}
