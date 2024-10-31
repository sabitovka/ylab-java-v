package io.sabitovka.auth;

import io.sabitovka.auth.entity.UserDetails;
import io.sabitovka.enums.ErrorCode;
import io.sabitovka.exception.ApplicationException;
import lombok.Getter;
import lombok.Setter;

public final class AuthInMemoryContext {
    private final static AuthInMemoryContext CONTEXT = new AuthInMemoryContext();

    private UserDetails authenticatedUser;

    @Setter
    @Getter
    private String ip;

    public static synchronized AuthInMemoryContext getContext() {
        return CONTEXT;
    }

    public UserDetails getAuthentication() {
        if (authenticatedUser == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED, "Ошибка авторизации. Выполните вход");
        }
        return authenticatedUser;
    }

    public boolean isLoggedIn() {
        return authenticatedUser != null;
    }

    public void setAuthentication(UserDetails user) {
        authenticatedUser = user;
    }
}
