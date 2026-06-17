package com.smartdiet.planner.service;

import com.smartdiet.planner.dto.MealDTO;
import com.smartdiet.planner.exception.ResourceNotFoundException;
import com.smartdiet.planner.model.FoodItem;
import com.smartdiet.planner.model.Meal;
import com.smartdiet.planner.repository.DietPlanRepository;
import com.smartdiet.planner.repository.FoodItemRepository;
import com.smartdiet.planner.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;
    private final DietPlanRepository dietPlanRepository;
    private final FoodItemRepository foodItemRepository;

    public MealDTO createMeal(MealDTO dto) {
        if (!dietPlanRepository.existsById(dto.getDietPlanId())) {
            throw new ResourceNotFoundException("Diet Plan not found with id: " + dto.getDietPlanId());
        }

        Meal meal = convertToEntity(dto);
        Meal saved = mealRepository.save(meal);
        return convertToDTO(saved);
    }

    public MealDTO getMealById(String mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + mealId));
        return convertToDTO(meal);
    }

    public List<MealDTO> getMealsByDietPlanId(String dietPlanId) {
        if (!dietPlanRepository.existsById(dietPlanId)) {
            throw new ResourceNotFoundException("Diet Plan not found with id: " + dietPlanId);
        }
        return mealRepository.findByDietPlanId(dietPlanId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MealDTO updateMeal(String mealId, MealDTO dto) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + mealId));

        meal.setMealType(dto.getMealType());
        meal.setTotalCalories(dto.getTotalCalories());

        Meal updated = mealRepository.save(meal);
        return convertToDTO(updated);
    }

    public void deleteMeal(String mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + mealId));

        // Cascade delete FoodItems
        foodItemRepository.deleteByMealId(mealId);

        // Delete Meal
        mealRepository.delete(meal);
    }

    public void recalculateMealCalories(String mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + mealId));

        List<FoodItem> foodItems = foodItemRepository.findByMealId(mealId);
        double totalCalories = foodItems.stream()
                .mapToDouble(food -> food.getCalories() * food.getQuantity())
                .sum();

        meal.setTotalCalories(Math.round(totalCalories * 100.0) / 100.0);
        mealRepository.save(meal);
    }

    private Meal convertToEntity(MealDTO dto) {
        return Meal.builder()
                .mealId(dto.getMealId())
                .mealType(dto.getMealType())
                .totalCalories(dto.getTotalCalories())
                .dietPlanId(dto.getDietPlanId())
                .build();
    }

    private MealDTO convertToDTO(Meal meal) {
        return MealDTO.builder()
                .mealId(meal.getMealId())
                .mealType(meal.getMealType())
                .totalCalories(meal.getTotalCalories())
                .dietPlanId(meal.getDietPlanId())
                .build();
    }
}
