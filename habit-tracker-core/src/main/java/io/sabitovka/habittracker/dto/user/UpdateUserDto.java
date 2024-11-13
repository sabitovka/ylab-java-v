package io.sabitovka.habittracker.dto.user;

import io.sabitovka.habittracker.util.validation.annotation.Email;
import io.sabitovka.habittracker.util.validation.annotation.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для обновления пользователя
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {
    @Name
    String name;

    @Email
    String email;
}
