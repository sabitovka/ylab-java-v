package io.sabitovka.habittracker.exception;

import io.sabitovka.habittracker.enums.ErrorCode;
import lombok.Getter;

/**
 * Исключение выбрасываемое приложением
 */
@Getter
public class ApplicationException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApplicationException(ErrorCode errorCode) {
        this(errorCode, "");
    }

    public ApplicationException(ErrorCode errorCode, String details) {
        super(details);
        this.errorCode = errorCode;
    }

    public ApplicationException(ErrorCode errorCode, Throwable throwable) {
        this(errorCode, throwable.getMessage());
    }
}
