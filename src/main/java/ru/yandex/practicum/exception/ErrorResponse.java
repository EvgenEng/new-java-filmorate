package ru.yandex.practicum.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class ErrorResponse {
    private final String message;
    private final String reason;
    private final HttpStatus status;
    private final LocalDateTime timestamp;
    private final Map<String, String> errors;

    @JsonCreator
    public ErrorResponse(
            @JsonProperty("message") String message,
            @JsonProperty("reason") String reason,
            @JsonProperty("status") HttpStatus status,
            @JsonProperty("timestamp") LocalDateTime timestamp,
            @JsonProperty("errors") Map<String, String> errors) {
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    public ErrorResponse(String message, String reason, HttpStatus status, LocalDateTime timestamp) {
        this(message, reason, status, timestamp, null);
    }

    public ErrorResponse(String message, String reason) {
        this(message, reason, HttpStatus.BAD_REQUEST, LocalDateTime.now(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(HttpMessageNotReadableException ex) {
        String message = "Invalid JSON format";
        if (ex.getCause() instanceof JsonParseException) {
            message = ex.getCause().getMessage();
        }
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("JSON parse error", message));
    }
}
