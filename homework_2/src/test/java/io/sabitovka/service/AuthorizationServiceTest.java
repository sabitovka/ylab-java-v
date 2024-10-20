package io.sabitovka.service;

import io.sabitovka.model.User;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.service.impl.AuthorizationServiceImpl;
import io.sabitovka.util.PasswordHasher;
import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест сервиса авторизации")
class AuthorizationServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationServiceImpl authorizationService;

    @Test
    @DisplayName("[login] Тест с корректными данными пользователя")
    public void loginWithValidCredentialsShouldSetCurrentUser() {
        User user = new User(1L, "mock", "mock@example.com", PasswordHasher.hash("password"), false, true);
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.of(user));

        authorizationService.login("mock@example.com", "password");

        assertThat(authorizationService.isLoggedIn()).isTrue();
        assertThat(authorizationService.getCurrentUserId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("[login] Не должен выполнить вход, если пароль неверный")
    public void loginWithInvalidPasswordShouldNotSetCurrentUser() {
        User user = new User(1L, "mock", "mock@example.com", PasswordHasher.hash("password"), false, true);
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.of(user));

        authorizationService.login("mock@example.com", "wrongPassword");

        assertThat(authorizationService.isLoggedIn()).isFalse();
        assertThat(authorizationService.getCurrentUserId()).isNull();
    }

    @Test
    @DisplayName("[login] Когда пользователя нет, не должен выполнять вход")
    public void loginWithNonExistentUserShouldNotSetCurrentUser() {
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.empty());

        authorizationService.login("mock@example.com", "password");

        assertThat(authorizationService.isLoggedIn()).isFalse();
        assertThat(authorizationService.getCurrentUserId()).isNull();
    }

    @Test
    @DisplayName("[login] Не должен выполнять вход для заблокированного пользователя")
    public void loginWithBlockedUserShouldThrowException() {
        User blockedUser = new User(1L, "mock", "mock@example.com", PasswordHasher.hash("password"), false, false);
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.of(blockedUser));

        assertThatThrownBy(() -> authorizationService.login("mock@example.com", "password"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ваша учетная запись была заблокирована");
    }

    @Test
    @DisplayName("[getCurrentUser] Когда выполнен вход, должен возвращать авторизованного пользователя")
    public void getCurrentUserWhenLoggedInShouldReturnCurrentUser() {
        User user = new User(1L, "mock", "mock@example.com", PasswordHasher.hash("password"), false, true);
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        authorizationService.login("mock@example.com", "password");

        User currentUser = authorizationService.getCurrentUser();

        assertThat(currentUser).isEqualTo(user);
    }

    @Test
    @DisplayName("[getCurrentUser] Когда не авторизован, должен возвращать исключение")
    public void getCurrentUserWhenNotLoggedInShouldThrowException() {
        assertThatThrownBy(() -> authorizationService.getCurrentUser())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ошибка авторизации. Выполните вход");
    }

    @Test
    @DisplayName("[isAdmin] Когда пользователь - админ - должен возвращать true")
    public void isAdminWhenUserIsAdminShouldReturnTrue() {
        User adminUser = new User(1L, "admin", "admin@example.com", PasswordHasher.hash("password"), true, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(userRepository.findUserByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        authorizationService.login("admin@example.com", "password");

        boolean isAdmin = authorizationService.isAdmin();

        assertThat(isAdmin).isTrue();
    }

    @Test
    @DisplayName("[isAdmin] Когда пользователь не админ, должен вернуть false")
    public void isAdminWhenUserIsNotAdminShouldReturnFalse() {
        User normalUser = new User(1L, "user", "user@example.com", PasswordHasher.hash("password"), false, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(normalUser));
        when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.of(normalUser));
        authorizationService.login("user@example.com", "password");

        boolean isAdmin = authorizationService.isAdmin();

        assertThat(isAdmin).isFalse();
    }

    @Test
    @DisplayName("[logout] Должен убрать текущего пользователя")
    public void logoutShouldClearCurrentUserId() {
        User user = new User(1L, "mock", "mock@example.com", PasswordHasher.hash("password"), false, true);
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.of(user));
        authorizationService.login("mock@example.com", "password");

        authorizationService.logout();

        assertThat(authorizationService.isLoggedIn()).isFalse();
        assertThat(authorizationService.getCurrentUserId()).isNull();
    }

    @Test
    @DisplayName("[isLoggedIn] Когда не авторизован, должен вернуть false")
    public void isLoggedInWhenNotLoggedInShouldReturnFalse() {
        assertThat(authorizationService.isLoggedIn()).isFalse();
    }
}
