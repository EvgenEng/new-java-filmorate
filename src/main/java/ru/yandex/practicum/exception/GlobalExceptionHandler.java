package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error instanceof FieldError ?
                                ((FieldError) error).getField() :
                                error.getObjectName(),
                        error -> error.getDefaultMessage() != null ?
                                error.getDefaultMessage() : "Validation error",
                        (existing, replacement) -> existing
                ));

        return buildErrorResponse(
                "Validation failed",
                "Invalid request parameters",
                HttpStatus.BAD_REQUEST,
                errors
        );
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        return buildErrorResponse(
                "Validation error",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                ex.getErrors()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return buildErrorResponse(
                ex.getMessage(),
                "Not Found",
                HttpStatus.NOT_FOUND,
                null
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(
                "Invalid data",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                null
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        return buildErrorResponse(
                ex.getReason(),
                ex.getReason(),
                HttpStatus.valueOf(ex.getStatusCode().value()),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        return buildErrorResponse(
                "Internal server error",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                null
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String message,
            String reason,
            HttpStatus status,
            Map<String, String> errors
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                message,
                reason,
                status,
                LocalDateTime.now(),
                errors != null ? errors : new HashMap<>()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }
}
