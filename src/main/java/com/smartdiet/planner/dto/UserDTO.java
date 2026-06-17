package com.smartdiet.planner.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String userId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Min(value = 1, message = "Age must be greater than 0")
    @Max(value = 150, message = "Age must be less than 150")
    private int age;

    @NotBlank(message = "Gender is required")
    private String gender;

    @Positive(message = "Height must be positive")
    private double height;

    @Positive(message = "Weight must be positive")
    private double weight;

    @NotBlank(message = "Goal is required (Weight Loss / Weight Gain / Maintenance)")
    private String goal;
}
