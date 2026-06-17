package com.smartdiet.planner.controller;

import com.smartdiet.planner.dto.MealDTO;
import com.smartdiet.planner.response.ApiResponse;
import com.smartdiet.planner.service.MealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@Tag(name = "Meal Management", description = "Endpoints for managing meals")
public class MealController {

    private final MealService mealService;

    @PostMapping
    @Operation(summary = "Create a new meal and add it to a diet plan")
    public ResponseEntity<ApiResponse<MealDTO>> createMeal(@Valid @RequestBody MealDTO dto) {
        MealDTO created = mealService.createMeal(dto);
        return new ResponseEntity<>(ApiResponse.success(created, "Meal created successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a meal by ID")
    public ResponseEntity<ApiResponse<MealDTO>> getMealById(@PathVariable String id) {
        MealDTO meal = mealService.getMealById(id);
        return ResponseEntity.ok(ApiResponse.success(meal, "Meal retrieved successfully"));
    }

    @GetMapping("/diet-plan/{dietPlanId}")
    @Operation(summary = "Get all meals for a specific diet plan")
    public ResponseEntity<ApiResponse<List<MealDTO>>> getMealsByDietPlanId(@PathVariable String dietPlanId) {
        List<MealDTO> meals = mealService.getMealsByDietPlanId(dietPlanId);
        return ResponseEntity.ok(ApiResponse.success(meals, "Meals retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing meal")
    public ResponseEntity<ApiResponse<MealDTO>> updateMeal(@PathVariable String id, @Valid @RequestBody MealDTO dto) {
        MealDTO updated = mealService.updateMeal(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Meal updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a meal (cascades deletes to food items)")
    public ResponseEntity<ApiResponse<Void>> deleteMeal(@PathVariable String id) {
        mealService.deleteMeal(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Meal deleted successfully"));
    }
}
