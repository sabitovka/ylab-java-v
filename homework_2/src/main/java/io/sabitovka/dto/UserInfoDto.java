package io.sabitovka.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInfoDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private boolean isAdmin;
    private boolean isActive = true;

    @Override
    public String toString() {
        return "Пользователь #" + id +
                " Имя: " + name +
                " Email " + email;
    }
}
