package com.smartdiet.planner.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "progress")
public class Progress {

    @Id
    private String progressId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @Positive(message = "Current weight must be positive")
    private double currentWeight;

    @PositiveOrZero(message = "BMI must be zero or positive")
    private double bmi;

    @NotNull(message = "Date is required")
    private LocalDate date;
}
