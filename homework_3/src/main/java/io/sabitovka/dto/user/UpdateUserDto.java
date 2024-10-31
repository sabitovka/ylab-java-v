package io.sabitovka.dto.user;

import io.sabitovka.util.validation.annotation.Email;
import io.sabitovka.util.validation.annotation.Name;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для обновления пользователя
 */
@Setter
@Getter
public class UpdateUserDto {
    @Name
    String name;

    @Email
    String email;
}
