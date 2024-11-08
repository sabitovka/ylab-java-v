package io.sabitovka.habittracker.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Перечисление всех кодов, которое может выдать приложение
 * Каждая ошибка связана по своему смыслу по ID. Так, например все ошибки об
 * отсутствии чего-либо (код 404) имеют ID 101, 102, ... N
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND(100, "Не найдено", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(101, "Пользователь не найден", HttpStatus.NOT_FOUND),
    HABIT_NOT_FOUND(102, "Привычка не найдена", HttpStatus.NOT_FOUND),
    BAD_REQUEST(200, "Ошибка запроса", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(300, "Ошибка авторизации", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(400, "Нет доступа", HttpStatus.FORBIDDEN),
    INTERNAL_ERROR(900, "Неизвестная внутренняя ошибка", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int id;
    private final String message;
    private final HttpStatus httpCode;
}
