package io.sabitovka.dto.user;

import io.sabitovka.util.validation.annotation.Email;
import io.sabitovka.util.validation.annotation.Name;
import io.sabitovka.util.validation.annotation.Password;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для создания пользователя
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDto {
    @Name
    private String name;

    @Email
    private String email;

    @Password
    private String password;
}
