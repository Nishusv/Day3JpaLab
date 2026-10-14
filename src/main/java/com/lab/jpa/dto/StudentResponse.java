package com.lab.jpa.dto;

import java.util.List;

public record StudentResponse(
        Long id,
        String name,
        String email,
        Integer age,
        String department,
        String phone,
        String address,
        List<String> courses) {
}
