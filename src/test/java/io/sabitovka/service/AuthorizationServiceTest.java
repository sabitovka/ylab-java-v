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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

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
    public void login_whenEmailNotFound_shouldReturnFalse() {
        String email = "user1@example.com";
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        boolean result = authorizationService.login(email, "password123");
        assertThat(result).isFalse();

        verify(userService, never()).setCurrentUser(any(User.class));
    }

    @Test
    public void login_whenPasswordIsIncorrect_shouldReturnFalse() {
        String email = "user@example.com";
        String password = "wrongPassword";
        User user = new User("user", email, PasswordHasher.hash("correctPassword"));
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        boolean result = authorizationService.login(email, password);
        assertThat(result).isFalse();

        verify(userService, never()).setCurrentUser(any(User.class));
    }

    @Test
    public void login_whenCredentialsAreCorrect_shouldReturnTrueAndSetCurrentUser() {
        String email = "user@example.com";
        String password = "correctPassword";
        User user = new User("user", email, PasswordHasher.hash(password));
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        boolean result = authorizationService.login(email, password);
        assertThat(result).isTrue();

        verify(userService, times(1)).setCurrentUser(user);
    }
}
