package com.smartdiet.planner.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "meals")
public class Meal {

    @Id
    private String mealId;

    @NotBlank(message = "Meal type is required (Breakfast, Lunch, Dinner, Snacks)")
    private String mealType;

    @PositiveOrZero(message = "Total calories must be zero or positive")
    private double totalCalories;

    @NotBlank(message = "Diet Plan ID is required")
    private String dietPlanId;
}
