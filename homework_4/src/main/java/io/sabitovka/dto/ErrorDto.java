package io.sabitovka.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для вывода ошибки
 */
@Data
@NoArgsConstructor
public class ErrorDto {
    private int internalCode;
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();
}
