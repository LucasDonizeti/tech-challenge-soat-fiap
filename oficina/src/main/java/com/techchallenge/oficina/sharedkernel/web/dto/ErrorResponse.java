package com.techchallenge.oficina.sharedkernel.web.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String errorCode,
    String path,
    List<FieldError> errors
) {

    public static ErrorResponse of(int status, String error, String message, String errorCode, String path) {
        return new ErrorResponse(
            LocalDateTime.now(),
            status,
            error,
            message,
            errorCode,
            path,
            null
        );
    }

    public static ErrorResponse of(int status, String error, String message, String errorCode, String path, List<FieldError> errors) {
        return new ErrorResponse(
            LocalDateTime.now(),
            status,
            error,
            message,
            errorCode,
            path,
            errors
        );
    }
}
