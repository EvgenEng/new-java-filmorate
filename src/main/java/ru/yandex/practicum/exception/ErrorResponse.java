package ru.yandex.practicum.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.http.HttpStatus;

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
}
