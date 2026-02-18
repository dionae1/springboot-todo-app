package com.example.demo.exception;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ArgumentNotValidException {
    private List<String> fields;
    private String message;
    private LocalDateTime timestamp;

    public ArgumentNotValidException(List<String> fields, String message) {
        this.fields = fields;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
