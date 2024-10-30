package io.sabitovka.dto.user;

import io.sabitovka.util.validation.annotation.Email;
import io.sabitovka.util.validation.annotation.Name;
import io.sabitovka.util.validation.annotation.Password;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateUserDto {
    @Name
    String name;

    @Email
    String email;
}
