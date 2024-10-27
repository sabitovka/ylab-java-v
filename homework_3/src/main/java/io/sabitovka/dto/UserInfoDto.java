package io.sabitovka.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInfoDto {
    private Long id;
    private String name;
    private String email;
}
