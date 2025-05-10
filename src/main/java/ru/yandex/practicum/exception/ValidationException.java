package ru.yandex.practicum.exception;

import java.util.Collections;
import java.util.Map;

public class ValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = Collections.emptyMap();
    }

    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors != null ? errors : Collections.emptyMap();
    }

    public ValidationException(String message, String field, String errorMessage) {
        super(message);
        this.errors = Map.of(field, errorMessage);
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
