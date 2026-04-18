package com.techchallenge.oficina.sharedkernel.web.exception;

import com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException;
import com.techchallenge.oficina.sharedkernel.web.dto.ErrorResponse;
import com.techchallenge.oficina.sharedkernel.web.dto.FieldError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex, WebRequest request) {
        log.error("DomainException: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            ex.getMessage(),
            ex.getErrorCode(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        
        List<FieldError> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
            .toList();
        
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Dados de entrada inválidos",
            "VALIDATION_ERROR",
            request.getDescription(false).replace("uri=", ""),
            fieldErrors
        );
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.error("Type mismatch error: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Parâmetro inválido: " + ex.getName(),
            "INVALID_PARAMETER",
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "Erro interno do servidor",
            "INTERNAL_ERROR",
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
