package io.sabitovka.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDetails {
    private Long userId;
    private String username;
    private boolean isAdmin;
}
