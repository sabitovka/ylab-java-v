package io.sabitovka.habittracker.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Сущность данных о пользователе, используемых для авторизации и аутентификации
 */
@Getter
@Setter
@AllArgsConstructor
public class UserDetails {
    private Long userId;
    private String username;
    private boolean isAdmin;
}
