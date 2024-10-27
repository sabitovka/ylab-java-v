package io.sabitovka.exception;

import java.util.List;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {
    private List<String> messages;

    public ValidationException(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public String getMessage() {
        return String.join("\n", messages);
    }
}
