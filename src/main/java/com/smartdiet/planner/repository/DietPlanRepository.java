package com.smartdiet.planner.repository;

import com.smartdiet.planner.model.DietPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DietPlanRepository extends MongoRepository<DietPlan, String> {
    List<DietPlan> findByUserId(String userId);
}
