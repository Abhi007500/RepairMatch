package com.repairmatch.modules.catalog.repository;

import com.repairmatch.modules.catalog.domain.ProblemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemTypeRepository extends JpaRepository<ProblemType, String> {
    List<ProblemType> findByCategoryIdOrderByTitleAsc(String categoryId);
}
