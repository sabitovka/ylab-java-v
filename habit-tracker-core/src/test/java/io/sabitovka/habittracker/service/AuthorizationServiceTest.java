package io.sabitovka.habittracker.service;

import io.sabitovka.habittracker.auth.util.Jwt;
import io.sabitovka.habittracker.auth.util.PasswordHasher;
import io.sabitovka.habittracker.dto.user.UserLoginDto;
import io.sabitovka.habittracker.exception.ApplicationException;
import io.sabitovka.habittracker.model.User;
import io.sabitovka.habittracker.repository.UserRepository;
import io.sabitovka.habittracker.service.impl.AuthorizationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayName("Тест сервиса авторизации")
@ExtendWith({ MockitoExtension.class })
class AuthorizationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private AuthorizationServiceImpl authorizationService;

    private final UserLoginDto userLoginDto = new UserLoginDto();

    @BeforeEach
    void setUp() {
        userLoginDto.setEmail("mock@example.com");
        userLoginDto.setPassword("password");
    }

    @Test
    @DisplayName("[login] Тест с корректными данными пользователя")
    public void loginWithValidCredentialsShouldSetCurrentUser() {
        when(passwordHasher.hash(anyString())).thenReturn("hashedPassword");
        when(passwordHasher.verify(anyString(), anyString())).thenReturn(true);
        when(jwt.generate(1L)).thenReturn("jwt-token");
        User user = new User(1L, "mock", "mock@example.com", passwordHasher.hash("password"), false, true);
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.of(user));

        String token = authorizationService.login(userLoginDto);

        assertThat(token).isNotNull();
        assertThat(token).isEqualTo("jwt-token");
    }

    @Test
    @DisplayName("[login] Не должен выполнить вход, если пароль неверный")
    public void loginWithInvalidPasswordShouldNotSetCurrentUser() {
        User user = new User(1L, "mock", "mock@example.com", passwordHasher.hash("password"), false, true);
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.of(user));

        userLoginDto.setPassword("wrongPassword");

        assertThatThrownBy(() -> authorizationService.login(userLoginDto))
                .isInstanceOf(ApplicationException.class);
    }

    @Test
    @DisplayName("[login] Когда пользователя нет, не должен выполнять вход")
    public void loginWithNonExistentUserShouldNotSetCurrentUser() {
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorizationService.login(userLoginDto))
                .isInstanceOf(ApplicationException.class);
    }

    @Test
    @DisplayName("[login] Не должен выполнять вход для заблокированного пользователя")
    public void loginWithBlockedUserShouldThrowException() {
        User blockedUser = new User(1L, "mock", "mock@example.com", passwordHasher.hash("password"), false, false);
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.of(blockedUser));

        assertThatThrownBy(() -> authorizationService.login(userLoginDto))
                .isInstanceOf(ApplicationException.class);
    }
}
