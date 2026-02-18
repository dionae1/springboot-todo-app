package com.example.demo.handler;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.demo.exception.ArgumentNotValidException;
import com.example.demo.exception.HttpException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<String> fields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .toList();

        ArgumentNotValidException response = new ArgumentNotValidException(fields,
                "Validation failed for one or more fields.");

        return ResponseEntity.badRequest()
                .body(response);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        String error = ex.getMessage();
        HttpException response = new HttpException(status.value(), error, "An unexpected error occurred.");

        return ResponseEntity.status(status.value()).body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        String error = ex.getReason();
        String message = ex.getLocalizedMessage();
        HttpStatusCode status = ex.getStatusCode();

        HttpException response = new HttpException(status.value(), error, message);

        return ResponseEntity.status(status.value()).body(response);
    }
}
