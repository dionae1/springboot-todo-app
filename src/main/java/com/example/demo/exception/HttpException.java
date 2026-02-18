package com.example.demo.exception;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class HttpException {
    private String error;
    private String message;
    private LocalDateTime timestamp;

    public HttpException(int status, String error, String message) {
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
