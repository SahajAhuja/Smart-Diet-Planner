package com.smartdiet.planner.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FoodItemDTO {
    private String foodId;

    @NotBlank(message = "Food name is required")
    private String foodName;

    @PositiveOrZero private double calories;
    @PositiveOrZero private double protein;
    @PositiveOrZero private double carbs;
    @PositiveOrZero private double fats;
    @Positive(message = "Quantity must be positive")
    private double quantity;

    @NotBlank(message = "Meal ID is required")
    private String mealId;
}
