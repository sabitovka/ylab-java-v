package io.sabitovka.dto.user;

import io.sabitovka.util.validation.annotation.Password;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для смены пароля
 */
@Getter
@Setter
public class ChangePasswordDto {
    @Password
    String oldPassword;

    @Password
    String newPassword;
}
