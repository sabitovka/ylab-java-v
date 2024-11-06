package io.sabitovka.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * DTO для вывода ошибки
 */
@Getter
@Setter
@NoArgsConstructor
public class ErrorDto {
    private int internalCode;
    private HttpStatus status;
    private String error;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();
}
