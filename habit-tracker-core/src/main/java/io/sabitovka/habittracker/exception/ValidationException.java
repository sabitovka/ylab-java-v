package io.sabitovka.habittracker.exception;

import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<String> messages;

    public ValidationException(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public String getMessage() {
        return String.join("\n", messages);
    }
}
