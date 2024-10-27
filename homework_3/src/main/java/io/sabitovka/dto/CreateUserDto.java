package io.sabitovka.dto;

import io.sabitovka.util.validation.annotation.ValidEmail;
import io.sabitovka.util.validation.annotation.ValidName;
import io.sabitovka.util.validation.annotation.ValidPassword;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUserDto {
    @ValidName
    private String name;

    @ValidEmail
    private String email;

    @ValidPassword
    private String password;
}
