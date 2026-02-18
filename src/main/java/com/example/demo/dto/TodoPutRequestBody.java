package com.example.demo.dto;

import com.example.demo.domain.TodoStage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TodoPutRequestBody(
        @NotBlank(message = "Title is required") String title,
        String description,
        @NotNull(message = "Stage is required") TodoStage stage) {
}
