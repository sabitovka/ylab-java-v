package io.sabitovka.habittracker.model;

import io.sabitovka.habittracker.persistence.annotation.Column;
import io.sabitovka.habittracker.persistence.annotation.Table;
import lombok.*;

/**
 * БД-сущность пользователя
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
@Table(name = "users")
public class User {
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "is_admin")
    private boolean isAdmin = false;

    @Column(name = "is_active")
    private boolean isActive = true;
}
