package io.sabitovka.controller;

import io.sabitovka.dto.ErrorDto;
import io.sabitovka.enums.ErrorCode;
import io.sabitovka.exception.ApplicationException;
import io.sabitovka.exception.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    private ErrorDto wrapToErrorDto(ApplicationException exception) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setInternalCode(exception.getErrorCode().getId());
        errorDto.setStatus(exception.getErrorCode().getHttpCode());
        errorDto.setError(exception.getErrorCode().getMessage());
        errorDto.setMessage(exception.getMessage());
        return errorDto;
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<?> handleApplicationException(ApplicationException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpCode())
                .body(wrapToErrorDto(e));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException e) {
        return ResponseEntity.badRequest()
                .body(wrapToErrorDto(new ApplicationException(ErrorCode.BAD_REQUEST, e)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.internalServerError()
                .body(wrapToErrorDto(new ApplicationException(ErrorCode.INTERNAL_ERROR, e.getMessage())));
    }

}
