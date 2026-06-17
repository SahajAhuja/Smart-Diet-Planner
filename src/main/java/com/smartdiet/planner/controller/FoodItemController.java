package com.smartdiet.planner.controller;

import com.smartdiet.planner.dto.FoodItemDTO;
import com.smartdiet.planner.response.ApiResponse;
import com.smartdiet.planner.service.FoodItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
@Tag(name = "Food Item Management", description = "Endpoints for managing food items inside meals")
public class FoodItemController {

    private final FoodItemService foodItemService;

    @PostMapping
    @Operation(summary = "Create a new food item and add it to a meal")
    public ResponseEntity<ApiResponse<FoodItemDTO>> createFoodItem(@Valid @RequestBody FoodItemDTO dto) {
        FoodItemDTO created = foodItemService.createFoodItem(dto);
        return new ResponseEntity<>(ApiResponse.success(created, "Food item created successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a food item by ID")
    public ResponseEntity<ApiResponse<FoodItemDTO>> getFoodItemById(@PathVariable String id) {
        FoodItemDTO foodItem = foodItemService.getFoodItemById(id);
        return ResponseEntity.ok(ApiResponse.success(foodItem, "Food item retrieved successfully"));
    }

    @GetMapping("/meal/{mealId}")
    @Operation(summary = "Get all food items for a specific meal")
    public ResponseEntity<ApiResponse<List<FoodItemDTO>>> getFoodItemsByMealId(@PathVariable String mealId) {
        List<FoodItemDTO> foods = foodItemService.getFoodItemsByMealId(mealId);
        return ResponseEntity.ok(ApiResponse.success(foods, "Food items retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing food item")
    public ResponseEntity<ApiResponse<FoodItemDTO>> updateFoodItem(@PathVariable String id, @Valid @RequestBody FoodItemDTO dto) {
        FoodItemDTO updated = foodItemService.updateFoodItem(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Food item updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a food item from a meal")
    public ResponseEntity<ApiResponse<Void>> deleteFoodItem(@PathVariable String id) {
        foodItemService.deleteFoodItem(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Food item deleted successfully"));
    }
}
