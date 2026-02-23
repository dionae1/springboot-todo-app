package com.example.demo.dto;

import java.util.List;

public record PageResponseBody<T>(
    List<T> content,
    int number,
    int size,
    long totalElements,
    int totalPages
) {
    
}
