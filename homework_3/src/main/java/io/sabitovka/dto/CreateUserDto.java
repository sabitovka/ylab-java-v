package io.sabitovka.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUserDto {
    private String name;
    private String email;
    private String password;
}
