package com.smartdiet.planner.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DietPlanDTO {
    private String planId;

    @NotBlank(message = "Plan name is required")
    private String planName;

    @Positive(message = "Calorie target must be positive")
    private double calorieTarget;

    @PositiveOrZero private double proteinTarget;
    @PositiveOrZero private double carbsTarget;
    @PositiveOrZero private double fatsTarget;

    @NotBlank(message = "User ID is required")
    private String userId;
}
