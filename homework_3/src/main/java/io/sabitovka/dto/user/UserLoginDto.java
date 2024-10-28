package io.sabitovka.dto.user;

import io.sabitovka.util.validation.annotation.Email;
import io.sabitovka.util.validation.annotation.Password;
import lombok.Data;

@Data
public class UserLoginDto {
    @Email
    private String email;

    @Password
    private String password;
}
