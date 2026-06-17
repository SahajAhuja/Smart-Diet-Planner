package com.smartdiet.planner.repository;

import com.smartdiet.planner.model.Progress;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProgressRepository extends MongoRepository<Progress, String> {
    List<Progress> findByUserId(String userId);
    List<Progress> findByUserIdOrderByDateDesc(String userId);
    void deleteByUserId(String userId);
}
