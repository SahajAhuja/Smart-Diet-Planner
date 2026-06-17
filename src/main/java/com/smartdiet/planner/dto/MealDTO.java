package com.smartdiet.planner.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MealDTO {
    private String mealId;

    @NotBlank(message = "Meal type is required")
    private String mealType;

    @PositiveOrZero private double totalCalories;

    @NotBlank(message = "Diet Plan ID is required")
    private String dietPlanId;
}
