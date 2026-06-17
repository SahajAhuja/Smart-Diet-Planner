package com.smartdiet.planner.repository;

import com.smartdiet.planner.model.FoodItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface FoodItemRepository extends MongoRepository<FoodItem, String> {
    List<FoodItem> findByMealId(String mealId);
    void deleteByMealId(String mealId);
}
