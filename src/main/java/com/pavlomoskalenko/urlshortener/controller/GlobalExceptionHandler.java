package com.pavlomoskalenko.urlshortener.controller;

import com.pavlomoskalenko.urlshortener.dto.ErrorResponse;
import com.pavlomoskalenko.urlshortener.exception.ShortUrlNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final HttpServletRequest httpServletRequest;

    public GlobalExceptionHandler(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getFieldErrors().stream()
                .filter((error) -> error.getDefaultMessage() != null)
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing));

        return ResponseEntity.badRequest().body(new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Invalid request content",
                httpServletRequest.getRequestURI(),
                fieldErrors
        ));
    }

    @ExceptionHandler(ShortUrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException ex) {
        return buildErrorResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponseEntity(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(
                LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), message, httpServletRequest.getRequestURI()));
    }
}
