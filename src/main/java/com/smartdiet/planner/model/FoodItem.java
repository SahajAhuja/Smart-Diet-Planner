package com.smartdiet.planner.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "food_items")
public class FoodItem {

    @Id
    private String foodId;

    @NotBlank(message = "Food name is required")
    private String foodName;

    @PositiveOrZero(message = "Calories must be zero or positive")
    private double calories;

    @PositiveOrZero(message = "Protein must be zero or positive")
    private double protein;

    @PositiveOrZero(message = "Carbs must be zero or positive")
    private double carbs;

    @PositiveOrZero(message = "Fats must be zero or positive")
    private double fats;

    @Positive(message = "Quantity must be positive")
    private double quantity;

    @NotBlank(message = "Meal ID is required")
    private String mealId;
}
