package io.sabitovka.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND(100, "Не найдено", 404),
    BAD_REQUEST(200, "Ошибка запроса", 400),
    UNAUTHORIZED(300, "Ошибка авторизации", 401),
    FORBIDDEN(400, "Нет доступа", 403),
    INTERNAL_ERROR(900, "Неизвестная внутренняя ошибка", 500);

    private final int id;
    private final String message;
    private final int httpCode;
}
