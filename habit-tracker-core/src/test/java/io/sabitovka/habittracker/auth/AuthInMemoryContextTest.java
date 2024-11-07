package io.sabitovka.habittracker.auth;

import io.sabitovka.habittracker.auth.entity.UserDetails;
import io.sabitovka.habittracker.exception.ApplicationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthInMemoryContextTest {
    private final AuthInMemoryContext context = AuthInMemoryContext.getContext();

    @AfterEach
    void resetContext() {
        context.setAuthentication(null);
        context.setIp(null);
    }

    @Test
    @DisplayName("[getAuthentication()] должен вернуть пользователя, если он авторизован")
    void testGetAuthenticationWhenUserIsAuthenticated() {
        UserDetails mockUser = new UserDetails(1L, "user", false);
        context.setAuthentication(mockUser);

        UserDetails result = context.getAuthentication();

        assertThat(result).isEqualTo(mockUser);
    }

    @Test
    @DisplayName("[getAuthentication()] должен выбросить исключение, если пользователь не авторизован")
    void testGetAuthenticationWhenUserNotAuthenticated() {
        context.setAuthentication(null);

        assertThatThrownBy(context::getAuthentication)
                .isInstanceOf(ApplicationException.class);
    }

    @Test
    @DisplayName("[isLoggedIn] должен возвращать true, если пользователь авторизован")
    void testIsLoggedInWhenUserIsAuthenticated() {
        context.setAuthentication(new UserDetails(1L, "user", false));

        assertThat(context.isLoggedIn()).isTrue();
    }

    @Test
    @DisplayName("[isLoggedIn()] должен возвращать false, если пользователь не авторизован")
    void testIsLoggedInWhenUserNotAuthenticated() {
        context.setAuthentication(null); // сбрасываем авторизацию

        assertThat(context.isLoggedIn()).isFalse();
    }

    @Test
    @DisplayName("[setAuthentication()] должен устанавливать пользователя")
    void testSetAuthentication() {
        UserDetails mockUser = new UserDetails(2L, "newUser", false);

        context.setAuthentication(mockUser);

        assertThat(context.getAuthentication()).isEqualTo(mockUser);
    }

    @Test
    @DisplayName("[setIp()] должен устанавливать IP-адрес")
    void testSetIp() {
        String mockIp = "192.168.0.1";

        context.setIp(mockIp);

        assertThat(context.getIp()).isEqualTo(mockIp);
    }
}
