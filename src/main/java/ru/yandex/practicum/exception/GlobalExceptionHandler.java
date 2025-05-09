package ru.yandex.practicum.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            WebRequest request
    ) {
        Throwable rootCause = ex.getRootCause();

        if (rootCause instanceof DateTimeParseException) {
            return handleDateTimeParseException((DateTimeParseException) rootCause, request);
        }

        if (rootCause instanceof InvalidFormatException) {
            return handleInvalidFormatException((InvalidFormatException) rootCause, request);
        }

        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid request format",
                "Malformed JSON request",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorResponse> handleDateTimeParseException(
            DateTimeParseException ex,
            WebRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Validation failed",
                "Invalid date format. Required format: yyyy-MM-dd",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFormatException(
            InvalidFormatException ex,
            WebRequest request
    ) {
        if (ex.getTargetType() != null && ex.getTargetType().equals(java.time.LocalDate.class)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Validation failed",
                    "Invalid date format. Required format: yyyy-MM-dd",
                    HttpStatus.BAD_REQUEST,
                    LocalDateTime.now()
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid request format",
                String.format("Invalid value '%s' for field %s",
                        ex.getValue(),
                        ex.getPath().get(0).getFieldName()),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Internal server error",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
