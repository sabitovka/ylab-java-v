package io.sabitovka.habittracker.dto.user;

import io.sabitovka.habittracker.util.validation.annotation.Email;
import io.sabitovka.habittracker.util.validation.annotation.Password;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для выполнения авторизации в системе
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {
    @Email
    private String email;

    @Password
    private String password;
}
