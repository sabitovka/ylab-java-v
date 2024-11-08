package io.sabitovka.habittracker.controller;

import io.sabitovka.habittracker.dto.ErrorDto;
import io.sabitovka.habittracker.enums.ErrorCode;
import io.sabitovka.habittracker.exception.ApplicationException;
import io.sabitovka.habittracker.exception.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * REST контроллер для обработки ошибок
 */
@RestControllerAdvice
public class ExceptionController {

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

    private ErrorDto wrapToErrorDto(ApplicationException exception) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setInternalCode(exception.getErrorCode().getId());
        errorDto.setStatus(exception.getErrorCode().getHttpCode());
        errorDto.setError(exception.getErrorCode().getMessage());
        errorDto.setMessage(exception.getMessage());
        return errorDto;
    }
}
