package com.lab.jpa.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StudentRequest(
        @NotBlank(message = "name is required") String name,
        @NotBlank(message = "email is required") @Email(message = "email must be valid") String email,
        @NotNull(message = "age is required")
        @Min(value = 3, message = "age must be at least 3")
        @Max(value = 100, message = "age must be at most 100") Integer age,
        Long departmentId,
        String phone,
        String address) {
}
