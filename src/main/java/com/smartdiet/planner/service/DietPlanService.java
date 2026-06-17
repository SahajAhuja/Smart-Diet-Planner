package com.smartdiet.planner.service;

import com.smartdiet.planner.dto.DietPlanDTO;
import com.smartdiet.planner.exception.ResourceNotFoundException;
import com.smartdiet.planner.model.DietPlan;
import com.smartdiet.planner.model.FoodItem;
import com.smartdiet.planner.model.Meal;
import com.smartdiet.planner.model.User;
import com.smartdiet.planner.repository.DietPlanRepository;
import com.smartdiet.planner.repository.FoodItemRepository;
import com.smartdiet.planner.repository.MealRepository;
import com.smartdiet.planner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DietPlanService {

    private final DietPlanRepository dietPlanRepository;
    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final FoodItemRepository foodItemRepository;

    public DietPlanDTO createDietPlan(DietPlanDTO dto) {
        if (!userRepository.existsById(dto.getUserId())) {
            throw new ResourceNotFoundException("User not found with id: " + dto.getUserId());
        }

        DietPlan dietPlan = convertToEntity(dto);
        dietPlan.setCreatedAt(Instant.now());
        dietPlan.setUpdatedAt(Instant.now());
        DietPlan saved = dietPlanRepository.save(dietPlan);
        return convertToDTO(saved);
    }

    public DietPlanDTO getDietPlanById(String planId) {
        DietPlan dietPlan = dietPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Diet Plan not found with id: " + planId));
        return convertToDTO(dietPlan);
    }

    public List<DietPlanDTO> getDietPlansByUserId(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return dietPlanRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DietPlanDTO updateDietPlan(String planId, DietPlanDTO dto) {
        DietPlan plan = dietPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Diet Plan not found with id: " + planId));

        plan.setPlanName(dto.getPlanName());
        plan.setCalorieTarget(dto.getCalorieTarget());
        plan.setProteinTarget(dto.getProteinTarget());
        plan.setCarbsTarget(dto.getCarbsTarget());
        plan.setFatsTarget(dto.getFatsTarget());
        plan.setUpdatedAt(Instant.now());

        DietPlan updated = dietPlanRepository.save(plan);
        return convertToDTO(updated);
    }

    public void deleteDietPlan(String planId) {
        DietPlan plan = dietPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Diet Plan not found with id: " + planId));

        // Cascade delete Meals & Foods
        List<Meal> meals = mealRepository.findByDietPlanId(planId);
        for (Meal meal : meals) {
            foodItemRepository.deleteByMealId(meal.getMealId());
        }
        mealRepository.deleteByDietPlanId(planId);

        // Delete Plan
        dietPlanRepository.delete(plan);
    }

    public DietPlanDTO generateRecommendedDiet(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Calculate Target Calories
        double bmr;
        boolean isMale = "male".equalsIgnoreCase(user.getGender()) || "m".equalsIgnoreCase(user.getGender());
        if (isMale) {
            bmr = (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) + 5;
        } else {
            bmr = (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) - 161;
        }

        double factor = 1.55; // default moderately active
        double tdee = bmr * factor;
        double targetCalories = tdee;

        String goal = user.getGoal() != null ? user.getGoal().toLowerCase() : "maintenance";
        if (goal.contains("loss") || goal.contains("lose")) {
            targetCalories = tdee - 500;
        } else if (goal.contains("gain")) {
            targetCalories = tdee + 500;
        }

        targetCalories = Math.round(targetCalories);

        // Standard Macros: Protein: 25%, Carbs: 45%, Fats: 30%
        double proteinGrams = Math.round((targetCalories * 0.25) / 4.0 * 10.0) / 10.0;
        double carbsGrams = Math.round((targetCalories * 0.45) / 4.0 * 10.0) / 10.0;
        double fatsGrams = Math.round((targetCalories * 0.30) / 9.0 * 10.0) / 10.0;

        // 1. Create DietPlan
        DietPlan plan = DietPlan.builder()
                .planName("Auto-Generated Recommended Plan (" + user.getGoal() + ")")
                .calorieTarget(targetCalories)
                .proteinTarget(proteinGrams)
                .carbsTarget(carbsGrams)
                .fatsTarget(fatsGrams)
                .userId(userId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        plan = dietPlanRepository.save(plan);

        // 2. Create Meals
        // Breakfast (30% calories)
        double bCalories = Math.round(targetCalories * 0.30);
        Meal breakfast = mealRepository.save(Meal.builder()
                .mealType("Breakfast")
                .totalCalories(bCalories)
                .dietPlanId(plan.getPlanId())
                .build());

        // Lunch (35% calories)
        double lCalories = Math.round(targetCalories * 0.35);
        Meal lunch = mealRepository.save(Meal.builder()
                .mealType("Lunch")
                .totalCalories(lCalories)
                .dietPlanId(plan.getPlanId())
                .build());

        // Dinner (25% calories)
        double dCalories = Math.round(targetCalories * 0.25);
        Meal dinner = mealRepository.save(Meal.builder()
                .mealType("Dinner")
                .totalCalories(dCalories)
                .dietPlanId(plan.getPlanId())
                .build());

        // Snacks (10% calories)
        double sCalories = Math.round(targetCalories * 0.10);
        Meal snacks = mealRepository.save(Meal.builder()
                .mealType("Snacks")
                .totalCalories(sCalories)
                .dietPlanId(plan.getPlanId())
                .build());

        // 3. Create Food Items under those Meals
        // Breakfast items: Oatmeal and Banana
        foodItemRepository.save(FoodItem.builder()
                .foodName("Rolled Oats")
                .calories(Math.round(bCalories * 0.65))
                .protein(Math.round(proteinGrams * 0.30 * 0.70 * 10.0) / 10.0)
                .carbs(Math.round(carbsGrams * 0.30 * 0.80 * 10.0) / 10.0)
                .fats(Math.round(fatsGrams * 0.30 * 0.50 * 10.0) / 10.0)
                .quantity(1.0)
                .mealId(breakfast.getMealId())
                .build());

        foodItemRepository.save(FoodItem.builder()
                .foodName("Banana")
                .calories(Math.round(bCalories * 0.35))
                .protein(Math.round(proteinGrams * 0.30 * 0.30 * 10.0) / 10.0)
                .carbs(Math.round(carbsGrams * 0.30 * 0.20 * 10.0) / 10.0)
                .fats(Math.round(fatsGrams * 0.30 * 0.50 * 10.0) / 10.0)
                .quantity(1.0)
                .mealId(breakfast.getMealId())
                .build());

        // Lunch items: Grilled Chicken Salad
        foodItemRepository.save(FoodItem.builder()
                .foodName("Grilled Chicken Breast")
                .calories(Math.round(lCalories * 0.70))
                .protein(Math.round(proteinGrams * 0.35 * 0.80 * 10.0) / 10.0)
                .carbs(Math.round(carbsGrams * 0.35 * 0.10 * 10.0) / 10.0)
                .fats(Math.round(fatsGrams * 0.35 * 0.30 * 10.0) / 10.0)
                .quantity(1.5)
                .mealId(lunch.getMealId())
                .build());

        foodItemRepository.save(FoodItem.builder()
                .foodName("Mixed Green Salad & Olive Oil")
                .calories(Math.round(lCalories * 0.30))
                .protein(Math.round(proteinGrams * 0.35 * 0.20 * 10.0) / 10.0)
                .carbs(Math.round(carbsGrams * 0.35 * 0.90 * 10.0) / 10.0)
                .fats(Math.round(fatsGrams * 0.35 * 0.70 * 10.0) / 10.0)
                .quantity(1.0)
                .mealId(lunch.getMealId())
                .build());

        // Dinner items: Salmon with Broccoli
        foodItemRepository.save(FoodItem.builder()
                .foodName("Baked Salmon Fillet")
                .calories(Math.round(dCalories * 0.75))
                .protein(Math.round(proteinGrams * 0.25 * 0.85 * 10.0) / 10.0)
                .carbs(Math.round(carbsGrams * 0.25 * 0.05 * 10.0) / 10.0)
                .fats(Math.round(fatsGrams * 0.25 * 0.80 * 10.0) / 10.0)
                .quantity(1.0)
                .mealId(dinner.getMealId())
                .build());

        foodItemRepository.save(FoodItem.builder()
                .foodName("Steamed Broccoli")
                .calories(Math.round(dCalories * 0.25))
                .protein(Math.round(proteinGrams * 0.25 * 0.15 * 10.0) / 10.0)
                .carbs(Math.round(carbsGrams * 0.25 * 0.95 * 10.0) / 10.0)
                .fats(Math.round(fatsGrams * 0.25 * 0.20 * 10.0) / 10.0)
                .quantity(2.0)
                .mealId(dinner.getMealId())
                .build());

        // Snacks items: Almonds
        foodItemRepository.save(FoodItem.builder()
                .foodName("Raw Almonds")
                .calories(sCalories)
                .protein(Math.round(proteinGrams * 0.10 * 10.0) / 10.0)
                .carbs(Math.round(carbsGrams * 0.10 * 10.0) / 10.0)
                .fats(Math.round(fatsGrams * 0.10 * 10.0) / 10.0)
                .quantity(1.0)
                .mealId(snacks.getMealId())
                .build());

        return convertToDTO(plan);
    }

    private DietPlan convertToEntity(DietPlanDTO dto) {
        return DietPlan.builder()
                .planId(dto.getPlanId())
                .planName(dto.getPlanName())
                .calorieTarget(dto.getCalorieTarget())
                .proteinTarget(dto.getProteinTarget())
                .carbsTarget(dto.getCarbsTarget())
                .fatsTarget(dto.getFatsTarget())
                .userId(dto.getUserId())
                .build();
    }

    private DietPlanDTO convertToDTO(DietPlan plan) {
        return DietPlanDTO.builder()
                .planId(plan.getPlanId())
                .planName(plan.getPlanName())
                .calorieTarget(plan.getCalorieTarget())
                .proteinTarget(plan.getProteinTarget())
                .carbsTarget(plan.getCarbsTarget())
                .fatsTarget(plan.getFatsTarget())
                .userId(plan.getUserId())
                .build();
    }
}
