package com.smartdiet.planner.controller;

import com.smartdiet.planner.dto.UserDTO;
import com.smartdiet.planner.response.ApiResponse;
import com.smartdiet.planner.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing users and performing calculators")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO created = userService.createUser(userDTO);
        return new ResponseEntity<>(ApiResponse.success(created, "User created successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable String id, @Valid @RequestBody UserDTO userDTO) {
        UserDTO updated = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "User updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user (cascades deletes to diet plans and progress)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }

    @GetMapping("/{id}/bmi")
    @Operation(summary = "Calculate BMI for a user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateBMI(@PathVariable String id) {
        Map<String, Object> bmiData = userService.calculateBMI(id);
        return ResponseEntity.ok(ApiResponse.success(bmiData, "BMI calculated successfully"));
    }

    @GetMapping("/{id}/calorie-calculator")
    @Operation(summary = "Calculate daily BMR, TDEE, and calorie target for a user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateDailyCalories(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "Moderately Active") String activityLevel) {
        Map<String, Object> calorieData = userService.calculateDailyCalories(id, activityLevel);
        return ResponseEntity.ok(ApiResponse.success(calorieData, "Daily calorie requirements calculated successfully"));
    }

    @GetMapping("/{id}/dashboard-stats")
    @Operation(summary = "Get user dashboard statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats(@PathVariable String id) {
        Map<String, Object> stats = userService.getDashboardStats(id);
        return ResponseEntity.ok(ApiResponse.success(stats, "Dashboard statistics retrieved successfully"));
    }
}
