package com.lab.jpa.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(@NotBlank(message = "name is required") String name) {
}
