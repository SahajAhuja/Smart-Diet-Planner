package com.smartdiet.planner.service;

import com.smartdiet.planner.dto.FoodItemDTO;
import com.smartdiet.planner.exception.ResourceNotFoundException;
import com.smartdiet.planner.model.FoodItem;
import com.smartdiet.planner.repository.FoodItemRepository;
import com.smartdiet.planner.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;
    private final MealRepository mealRepository;
    private final MealService mealService;

    public FoodItemDTO createFoodItem(FoodItemDTO dto) {
        if (!mealRepository.existsById(dto.getMealId())) {
            throw new ResourceNotFoundException("Meal not found with id: " + dto.getMealId());
        }

        FoodItem foodItem = convertToEntity(dto);
        FoodItem saved = foodItemRepository.save(foodItem);

        // Recalculate total calories of the parent meal
        mealService.recalculateMealCalories(dto.getMealId());

        return convertToDTO(saved);
    }

    public FoodItemDTO getFoodItemById(String foodId) {
        FoodItem foodItem = foodItemRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food Item not found with id: " + foodId));
        return convertToDTO(foodItem);
    }

    public List<FoodItemDTO> getFoodItemsByMealId(String mealId) {
        if (!mealRepository.existsById(mealId)) {
            throw new ResourceNotFoundException("Meal not found with id: " + mealId);
        }
        return foodItemRepository.findByMealId(mealId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FoodItemDTO updateFoodItem(String foodId, FoodItemDTO dto) {
        FoodItem food = foodItemRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food Item not found with id: " + foodId));

        food.setFoodName(dto.getFoodName());
        food.setCalories(dto.getCalories());
        food.setProtein(dto.getProtein());
        food.setCarbs(dto.getCarbs());
        food.setFats(dto.getFats());
        food.setQuantity(dto.getQuantity());

        FoodItem updated = foodItemRepository.save(food);

        // Recalculate total calories of the parent meal
        mealService.recalculateMealCalories(food.getMealId());

        return convertToDTO(updated);
    }

    public void deleteFoodItem(String foodId) {
        FoodItem food = foodItemRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food Item not found with id: " + foodId));

        String mealId = food.getMealId();
        foodItemRepository.delete(food);

        // Recalculate total calories of the parent meal
        mealService.recalculateMealCalories(mealId);
    }

    private FoodItem convertToEntity(FoodItemDTO dto) {
        return FoodItem.builder()
                .foodId(dto.getFoodId())
                .foodName(dto.getFoodName())
                .calories(dto.getCalories())
                .protein(dto.getProtein())
                .carbs(dto.getCarbs())
                .fats(dto.getFats())
                .quantity(dto.getQuantity())
                .mealId(dto.getMealId())
                .build();
    }

    private FoodItemDTO convertToDTO(FoodItem food) {
        return FoodItemDTO.builder()
                .foodId(food.getFoodId())
                .foodName(food.getFoodName())
                .calories(food.getCalories())
                .protein(food.getProtein())
                .carbs(food.getCarbs())
                .fats(food.getFats())
                .quantity(food.getQuantity())
                .mealId(food.getMealId())
                .build();
    }
}
