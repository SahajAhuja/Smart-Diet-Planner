package com.smartdiet.planner.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "diet_plans")
public class DietPlan {

    @Id
    private String planId;

    @NotBlank(message = "Plan name is required")
    private String planName;

    @Positive(message = "Calorie target must be positive")
    private double calorieTarget;

    @PositiveOrZero(message = "Protein target must be zero or positive")
    private double proteinTarget;

    @PositiveOrZero(message = "Carbs target must be zero or positive")
    private double carbsTarget;

    @PositiveOrZero(message = "Fats target must be zero or positive")
    private double fatsTarget;

    @NotBlank(message = "User ID is required")
    private String userId;

    private Instant createdAt;
    private Instant updatedAt;
}
