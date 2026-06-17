package com.smartdiet.planner.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgressDTO {
    private String progressId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @Positive(message = "Current weight must be positive")
    private double currentWeight;

    private double bmi;

    @NotNull(message = "Date is required")
    private LocalDate date;
}
