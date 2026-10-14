package com.lab.jpa.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseRequest(
        @NotBlank(message = "title is required") String title,
        @NotNull(message = "credits is required")
        @Min(value = 1, message = "credits must be at least 1")
        @Max(value = 10, message = "credits must be at most 10") Integer credits) {
}
