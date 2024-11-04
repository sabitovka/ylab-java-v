package io.sabitovka.dto.user;

import io.sabitovka.util.validation.annotation.Email;
import io.sabitovka.util.validation.annotation.Name;
import io.sabitovka.util.validation.annotation.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO с информацией о пользователе
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    @NotNull
    private Long id;
    @Name
    private String name;
    @Email
    private String email;
}
