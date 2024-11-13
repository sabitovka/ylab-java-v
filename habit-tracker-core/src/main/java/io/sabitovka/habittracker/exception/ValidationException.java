package io.sabitovka.habittracker.exception;

import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Исключение выбрасываемое валидатором
 */
@RequiredArgsConstructor
public class ValidationException extends RuntimeException {
    private final List<String> messages;

    @Override
    public String getMessage() {
        return String.join("\n", messages);
    }
}
