package io.sabitovka.service;

import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.model.User;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.util.PasswordHasher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    public void login_withValidCredentials_shouldSetCurrentUser() {
        User user = new User(1L, "mock", "mock@example.com", PasswordHasher.hash("password"), false, true);
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.of(user));

        authorizationService.login("mock@example.com", "password");

        assertThat(authorizationService.isLoggedIn()).isTrue();
        assertThat(authorizationService.getCurrentUserId()).isEqualTo(user.getId());
    }

    @Test
    public void login_withInvalidPassword_shouldNotSetCurrentUser() {
        User user = new User(1L, "mock", "mock@example.com", PasswordHasher.hash("password"), false, true);
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.of(user));

        authorizationService.login("mock@example.com", "wrongPassword");

        assertThat(authorizationService.isLoggedIn()).isFalse();
        assertThat(authorizationService.getCurrentUserId()).isNull();
    }

    @Test
    public void login_withNonExistentUser_shouldNotSetCurrentUser() {
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.empty());

        authorizationService.login("mock@example.com", "password");

        assertThat(authorizationService.isLoggedIn()).isFalse();
        assertThat(authorizationService.getCurrentUserId()).isNull();
    }

    @Test
    public void login_withBlockedUser_shouldThrowException() {
        User blockedUser = new User(1L, "mock", "mock@example.com", PasswordHasher.hash("password"), false, false);
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.of(blockedUser));

        assertThatThrownBy(() -> authorizationService.login("mock@example.com", "password"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ваша учетная запись была заблокирована");
    }

    @Test
    public void getCurrentUser_whenLoggedIn_shouldReturnCurrentUser() {
        User user = new User(1L, "mock", "mock@example.com", PasswordHasher.hash("password"), false, true);
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        authorizationService.login("mock@example.com", "password");

        User currentUser = authorizationService.getCurrentUser();

        assertThat(currentUser).isEqualTo(user);
    }

    @Test
    public void getCurrentUser_whenNotLoggedIn_shouldThrowException() {
        assertThatThrownBy(() -> authorizationService.getCurrentUser())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ошибка авторизации. Выполните вход");
    }

    @Test
    public void isAdmin_whenUserIsAdmin_shouldReturnTrue() {
        User adminUser = new User(1L, "admin", "admin@example.com", PasswordHasher.hash("password"), true, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(userRepository.findUserByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        authorizationService.login("admin@example.com", "password");

        boolean isAdmin = authorizationService.isAdmin();

        assertThat(isAdmin).isTrue();
    }

    @Test
    public void isAdmin_whenUserIsNotAdmin_shouldReturnFalse() {
        User normalUser = new User(1L, "user", "user@example.com", PasswordHasher.hash("password"), false, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(normalUser));
        when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.of(normalUser));
        authorizationService.login("user@example.com", "password");

        boolean isAdmin = authorizationService.isAdmin();

        assertThat(isAdmin).isFalse();
    }

    @Test
    public void logout_shouldClearCurrentUserId() {
        User user = new User(1L, "mock", "mock@example.com", PasswordHasher.hash("password"), false, true);
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.of(user));
        authorizationService.login("mock@example.com", "password");

        authorizationService.logout();

        assertThat(authorizationService.isLoggedIn()).isFalse();
        assertThat(authorizationService.getCurrentUserId()).isNull();
    }

    @Test
    public void isLoggedIn_whenNotLoggedIn_shouldReturnFalse() {
        assertThat(authorizationService.isLoggedIn()).isFalse();
    }
}
