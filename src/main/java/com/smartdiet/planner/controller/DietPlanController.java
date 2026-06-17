package com.smartdiet.planner.controller;

import com.smartdiet.planner.dto.DietPlanDTO;
import com.smartdiet.planner.response.ApiResponse;
import com.smartdiet.planner.service.DietPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diet-plans")
@RequiredArgsConstructor
@Tag(name = "Diet Plan Management", description = "Endpoints for managing diet plans and recommended diet generation")
public class DietPlanController {

    private final DietPlanService dietPlanService;

    @PostMapping
    @Operation(summary = "Create a new diet plan")
    public ResponseEntity<ApiResponse<DietPlanDTO>> createDietPlan(@Valid @RequestBody DietPlanDTO dto) {
        DietPlanDTO created = dietPlanService.createDietPlan(dto);
        return new ResponseEntity<>(ApiResponse.success(created, "Diet plan created successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a diet plan by ID")
    public ResponseEntity<ApiResponse<DietPlanDTO>> getDietPlanById(@PathVariable String id) {
        DietPlanDTO dietPlan = dietPlanService.getDietPlanById(id);
        return ResponseEntity.ok(ApiResponse.success(dietPlan, "Diet plan retrieved successfully"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all diet plans for a specific user")
    public ResponseEntity<ApiResponse<List<DietPlanDTO>>> getDietPlansByUserId(@PathVariable String userId) {
        List<DietPlanDTO> plans = dietPlanService.getDietPlansByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(plans, "Diet plans retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing diet plan")
    public ResponseEntity<ApiResponse<DietPlanDTO>> updateDietPlan(@PathVariable String id, @Valid @RequestBody DietPlanDTO dto) {
        DietPlanDTO updated = dietPlanService.updateDietPlan(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Diet plan updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a diet plan (cascades deletes to meals and food items)")
    public ResponseEntity<ApiResponse<Void>> deleteDietPlan(@PathVariable String id) {
        dietPlanService.deleteDietPlan(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Diet plan deleted successfully"));
    }

    @GetMapping("/user/{userId}/recommend")
    @Operation(summary = "Generate a recommended diet plan based on user profiles and BMR")
    public ResponseEntity<ApiResponse<DietPlanDTO>> generateRecommendedDiet(@PathVariable String userId) {
        DietPlanDTO plan = dietPlanService.generateRecommendedDiet(userId);
        return ResponseEntity.ok(ApiResponse.success(plan, "Recommended diet plan generated successfully"));
    }
}
