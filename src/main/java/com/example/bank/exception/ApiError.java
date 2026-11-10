package com.example.bank.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> validationErrors
) {
    public static ApiError of(HttpStatus status, String message) {
        return of(status, message, Map.of());
    }

    public static ApiError of(HttpStatus status, String message, Map<String, String> validationErrors) {
        return new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                validationErrors
        );
    }
}
