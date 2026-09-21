package com.repairmatch.modules.catalog.repository;

import com.repairmatch.modules.catalog.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findBySlug(String slug);
    List<Category> findAllByOrderByDisplayOrderAsc();

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.brands WHERE c.id = :id")
    Optional<Category> findByIdWithBrands(String id);
}
