package com.bankease.account.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String errorCode,
    String error,
    String message,
    String path,
    Map<String, String> validationErrors
) {
    public static ErrorResponse of(int status, String errorCode, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, errorCode, error, message, path, null);
    }

    public static ErrorResponse validation(int status, String errorCode, String error, String message, String path, Map<String, String> validationErrors) {
        return new ErrorResponse(LocalDateTime.now(), status, errorCode, error, message, path, validationErrors);
    }
}
