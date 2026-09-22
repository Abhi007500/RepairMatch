package com.repairmatch.modules.catalog.repository;

import com.repairmatch.modules.catalog.domain.ProblemType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemTypeRepository extends MongoRepository<ProblemType, String> {
    List<ProblemType> findByCategoryIdOrderByTitleAsc(String categoryId);
}
