package io.sabitovka.habittracker.service;

import io.sabitovka.habittracker.annotation.IgnoreAudit;
import io.sabitovka.habittracker.dto.user.UserLoginDto;

/**
 * Сервис для управления авторизацией в системе. Предоставляет основные методы для авторизации и аутентификации
 */
public interface AuthorizationService {
    /**
     * Генерирует токен при успешном входе в систему по переданным логину и паролю
     * @param userLoginDto Данные для входа в систему
     * @return Токен авторизации
     */
    String login(@IgnoreAudit UserLoginDto userLoginDto);
}
