package io.sabitovka.exception;

import io.sabitovka.enums.ErrorCode;
import lombok.Getter;

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