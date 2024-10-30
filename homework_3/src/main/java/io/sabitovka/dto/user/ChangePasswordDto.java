package io.sabitovka.dto.user;

import io.sabitovka.util.validation.annotation.Password;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {
    @Password
    String oldPassword;

    @Password
    String newPassword;
}
