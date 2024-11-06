package io.sabitovka.service;

import io.sabitovka.annotation.IgnoreAudit;
import io.sabitovka.dto.user.UserLoginDto;

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
