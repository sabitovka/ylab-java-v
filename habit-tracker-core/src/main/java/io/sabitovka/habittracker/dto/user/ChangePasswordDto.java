package io.sabitovka.habittracker.dto.user;

import io.sabitovka.habittracker.util.validation.annotation.Password;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для смены пароля
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDto {
    @Password
    String oldPassword;

    @Password
    String newPassword;
}
