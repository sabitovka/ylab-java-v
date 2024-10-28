package io.sabitovka.dto.user;

import io.sabitovka.util.validation.annotation.Email;
import io.sabitovka.util.validation.annotation.Name;
import io.sabitovka.util.validation.annotation.Password;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUserDto {
    @Name
    private String name;

    @Email
    private String email;

    @Password
    private String password;
}
