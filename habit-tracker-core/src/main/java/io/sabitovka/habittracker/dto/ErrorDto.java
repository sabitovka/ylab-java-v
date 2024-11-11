package io.sabitovka.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Модель ошибки")
public class ErrorDto {
    @Schema(description = "Внутрисистемный код ошибки")
    private int internalCode;

    @Schema(description = "HTTP-статус ошибки")
    private HttpStatus status;

    @Schema(description = "Общий текст ошибки")
    private String error;

    @Schema(description = "Детальное сообщение об ошибке")
    private String message;

    @Schema(description = "Время формирования ошибки")
    private LocalDateTime timestamp = LocalDateTime.now();
}
