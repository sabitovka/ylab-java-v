package io.sabitovka.service;

import io.sabitovka.dto.user.UserLoginDto;
import io.sabitovka.util.logging.annotation.Audit;
import io.sabitovka.util.logging.annotation.IgnoreAudit;

/**
 * Сервис для управления авторизацией в системе. Предоставляет основные методы для авторизации и аутентификации
 */
public interface AuthorizationService {
    /**
     * Генерирует токен при успешном входе в систему по переданным логину и паролю
     * @param email Логин или email пользователя
     * @param password Пароль пользователя
     * @return Токен авторизации
     */
    @Audit(action = "Выполнен вход в систему")
    String login(@IgnoreAudit UserLoginDto userLoginDto);
}
