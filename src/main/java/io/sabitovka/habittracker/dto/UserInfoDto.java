package io.sabitovka.habittracker.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
