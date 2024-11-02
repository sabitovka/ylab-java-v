package io.sabitovka.service;

import io.sabitovka.auth.util.PasswordHasher;
import io.sabitovka.dto.user.UserLoginDto;
import io.sabitovka.exception.ApplicationException;
import io.sabitovka.model.User;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.service.impl.AuthorizationServiceImpl;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест сервиса авторизации")
class AuthorizationServiceTest {
    @Mock
    private UserRepository userRepository;

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
        User user = new User(1L, "mock", "mock@example.com", PasswordHasher.hash("password"), false, true);
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.of(user));

        String token = authorizationService.login(userLoginDto);

        assertThat(token).isNotNull();
    }

    @Test
    @DisplayName("[login] Не должен выполнить вход, если пароль неверный")
    public void loginWithInvalidPasswordShouldNotSetCurrentUser() {
        User user = new User(1L, "mock", "mock@example.com", PasswordHasher.hash("password"), false, true);
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
        User blockedUser = new User(1L, "mock", "mock@example.com", PasswordHasher.hash("password"), false, false);
        when(userRepository.findUserByEmail("mock@example.com")).thenReturn(Optional.of(blockedUser));

        assertThatThrownBy(() -> authorizationService.login(userLoginDto))
                .isInstanceOf(ApplicationException.class);
    }
}
