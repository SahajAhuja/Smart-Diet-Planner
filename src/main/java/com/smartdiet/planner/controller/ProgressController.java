package com.smartdiet.planner.controller;

import com.smartdiet.planner.dto.ProgressDTO;
import com.smartdiet.planner.response.ApiResponse;
import com.smartdiet.planner.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@Tag(name = "Progress Tracking", description = "Endpoints for logging and tracking user weight progress")
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping
    @Operation(summary = "Log progress manually")
    public ResponseEntity<ApiResponse<ProgressDTO>> createProgress(@Valid @RequestBody ProgressDTO dto) {
        ProgressDTO created = progressService.createProgress(dto);
        return new ResponseEntity<>(ApiResponse.success(created, "Progress record created successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get progress record by ID")
    public ResponseEntity<ApiResponse<ProgressDTO>> getProgressById(@PathVariable String id) {
        ProgressDTO progress = progressService.getProgressById(id);
        return ResponseEntity.ok(ApiResponse.success(progress, "Progress record retrieved successfully"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all progress records for a user")
    public ResponseEntity<ApiResponse<List<ProgressDTO>>> getProgressByUserId(@PathVariable String userId) {
        List<ProgressDTO> progressList = progressService.getProgressByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(progressList, "Progress records retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete progress record by ID")
    public ResponseEntity<ApiResponse<Void>> deleteProgress(@PathVariable String id) {
        progressService.deleteProgress(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Progress record deleted successfully"));
    }

    @PostMapping("/user/{userId}/track-weight")
    @Operation(summary = "Track new weight (updates user weight profile and creates a progress entry)")
    public ResponseEntity<ApiResponse<ProgressDTO>> trackWeight(
            @PathVariable String userId,
            @RequestParam double currentWeight) {
        ProgressDTO created = progressService.trackWeight(userId, currentWeight);
        return ResponseEntity.ok(ApiResponse.success(created, "Weight tracked and profile updated successfully"));
    }
}
