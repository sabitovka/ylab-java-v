package io.sabitovka.habittracker.service;

import io.sabitovka.habittracker.model.User;

/**
 * Сервис для управления авторизацией в системе. Предоставляет основные методы для авторизации и аутентификации
 */
public interface AuthorizationService {
    /**
     * Возвращает авторизованного пользователя, который ранее выполнил вход
     * @return Авторизованный пользователь
     */
    User getCurrentUser();

    /**
     * Проверяет, является ли пользователь администратором
     * @return true - если да, false в ином случае
     */
    boolean isAdmin();

    /**
     * Получает ID текущего авторизованного пользователя
     * @return Идентификатор пользователя
     */
    Long getCurrentUserId();

    /**
     * Выполняет вход в систему по переданному логину и паролю
     * @param email Логин или email пользователя
     * @param password Пароль пользователя
     */
    void login(String email, String password);

    /**
     * Выполняет выход из системы, удаляет currentUser
     */
    void logout();

    /**
     * Проверяет, выполнен ли вход в систему пользователем
     * @return true - если пользователь авторизован, false - иначе
     */
    boolean isLoggedIn();
}
