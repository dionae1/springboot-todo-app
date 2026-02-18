package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TodoPostRequestBody(
        @NotNull(message = "User ID is required") Long userId,
        @NotBlank(message = "Title is required") String title,
        String description) {
}
