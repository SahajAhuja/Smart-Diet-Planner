package com.smartdiet.planner.repository;

import com.smartdiet.planner.model.Meal;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MealRepository extends MongoRepository<Meal, String> {
    List<Meal> findByDietPlanId(String dietPlanId);
    void deleteByDietPlanId(String dietPlanId);
}
