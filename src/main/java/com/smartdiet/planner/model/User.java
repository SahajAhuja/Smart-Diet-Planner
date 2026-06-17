package com.smartdiet.planner.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    @Id
    private String userId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
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

    private Instant createdAt;
    private Instant updatedAt;
}
