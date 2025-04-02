package ru.yandex.practicum.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException e) {
        log.error("Ошибка валидации: {}", e.getMessage());
        Map<String, Object> response = createErrorResponse(
                e.getMessage(),
                "Bad Request",
                HttpStatus.BAD_REQUEST
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException e) {
        log.error("Объект не найден: {}", e.getMessage());
        Map<String, Object> response = createErrorResponse(
                e.getMessage(),
                "Not Found",
                HttpStatus.NOT_FOUND
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception e) {
        log.error("Внутренняя ошибка сервера: {}", e.getMessage(), e);
        Map<String, Object> response = createErrorResponse(
                "Internal Server Error",
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> createErrorResponse(String message, String error, HttpStatus status) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        response.put("path", ""); // Можно добавить реальный path из запроса
        return response;
    }
}
