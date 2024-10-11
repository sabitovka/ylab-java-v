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
    public void register_whenEmailIsAlreadyExists_shouldThrowException() {
        String email = "existing@example.com";
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(new User("user1", email, "password")));

        assertThatThrownBy(() -> authorizationService.register("user2", email, "password123"))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("Данный email уже занят");

        verify(userRepository, never()).create(any(User.class));
    }

    @Test
    public void register_whenEmailIsUnique_shouldRegisterUser() {
        String email = "user1@example.com";
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        authorizationService.register("mockName", email, "password123");

        verify(userRepository, times(1)).create(any(User.class));
    }

    @Test
    public void login_whenEmailNotFound_shouldNotSetCurrentUser() {
        String email = "user1@example.com";
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        authorizationService.login(email, "password123");

        assertThat(authorizationService.isLoggedIn()).isFalse();
    }

    @Test
    public void login_whenPasswordIsIncorrect_shouldNotSetUser() {
        String email = "user@example.com";
        String password = "wrongPassword";
        User user = new User("user", email, PasswordHasher.hash("correctPassword"));
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        authorizationService.login(email, password);

        assertThat(authorizationService.isLoggedIn()).isFalse();
    }

    @Test
    public void login_whenCredentialsAreCorrect_shouldSetCurrentUser() {
        String email = "user@example.com";
        String password = "correctPassword";
        User user = new User("user", email, PasswordHasher.hash(password));
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        authorizationService.login(email, password);

        assertThat(authorizationService.isLoggedIn()).isTrue();
        assertThat(authorizationService.getCurrentUser()).isEqualTo(user);
    }

    @Test
    public void logout_whenLogout_shouldUnsetCurrentUser() {
        String email = "user@example.com";
        String password = "correctPassword";
        User user = new User("user", email, PasswordHasher.hash(password));
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        authorizationService.login(email, password);
        assertThat(authorizationService.isLoggedIn()).isTrue();

        authorizationService.logout();
        assertThat(authorizationService.isLoggedIn()).isFalse();
    }

    @Test
    public void getCurrentUser_whenNoUserLoggedIn_shouldThrowException() {
        assertThatThrownBy(() -> authorizationService.getCurrentUser())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Нет авторизованного пользователя");
    }
}
