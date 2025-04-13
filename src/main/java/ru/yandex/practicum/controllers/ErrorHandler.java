package ru.yandex.practicum.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.exception.ErrorResponse;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException exception) {
        log.error("Ошибка валидации: {}", exception.getMessage());
        return new ErrorResponse(
                exception.getMessage(),
                "Bad Request",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException exception) {
        log.error("Объект не найден: {}", exception.getMessage());
        return new ErrorResponse(
                exception.getMessage(),
                "Not Found",
                HttpStatus.NOT_FOUND,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllExceptions(Exception exception) {
        log.error("Внутренняя ошибка сервера: {}", exception.getMessage(), exception);
        return new ErrorResponse(
                "Internal Server Error",
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        String message = "Ошибка валидации";
        String reason = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((s1, s2) -> s1 + "; " + s2)
                .orElse("Неизвестная ошибка валидации");

        return new ErrorResponse(message, reason, HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalError(Throwable e) {
        return new ErrorResponse(
                "Произошла непредвиденная ошибка",
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now()
        );
    }
}
