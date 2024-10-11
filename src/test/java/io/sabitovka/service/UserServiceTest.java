package io.sabitovka.service;

import io.sabitovka.model.User;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.util.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;
    @InjectMocks
    private UserService userService;


    private User currentUser;

    @BeforeEach
    public void setUp() {
        currentUser = new User(1L, "user", "user@example.com", PasswordHasher.hash("password123"));
        userService.setCurrentUser(currentUser);
    }

    @Test
    public void changeName_whenNameIsValid_shouldChangeNameAndCallUpdate() {
        String newName = "user";

        userService.changeName(newName);

        assertThat(currentUser.getName()).isEqualTo(newName.trim());
        verify(userRepository, times(1)).update(currentUser);
    }

    @Test
    void changeName_whenNameIsNullOrBlank_shouldThrowException() {
        assertThatThrownBy(() -> userService.changeName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Новое имя пользователя не может быть пустым");

        assertThatThrownBy(() -> userService.changeName("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Новое имя пользователя не может быть пустым");
    }

    @Test
    void changeEmail_whenEmailIsValid_shouldChangeEmailAndCallUpdate() {
        String newEmail = "user@example.com";
        when(userRepository.findUserByEmail(newEmail)).thenReturn(Optional.empty());

        userService.changeEmail(newEmail);

        assertThat(currentUser.getEmail()).isEqualTo(newEmail.trim());
        verify(userRepository, times(1)).update(any(User.class));
    }

    @Test
    void changeEmail_whenEmailIsNullOrBlank_shouldThrowException() {
        assertThatThrownBy(() -> userService.changeEmail(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Новый email не может быть пустым");

        assertThatThrownBy(() -> userService.changeEmail("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Новый email не может быть пустым");
    }

    @Test
    void changeEmail_whenUserIsNotAuthorized_shouldThrowException() {
        userService.setCurrentUser(null);

        assertThatThrownBy(() -> userService.changeEmail("user@example.com"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Пользователь не авторизован");
    }

    @Test
    void changeEmail_whenEmailIsInvalidFormat_shouldThrowException() {
        String invalidEmail = "invalid-email";

        assertThatThrownBy(() -> userService.changeEmail(invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Некорректный формат email");
    }

    @Test
    void changeEmail_whenEmailAlreadyExists_shouldThrowException() {
        String existingEmail = "user@example.com";
        when(userRepository.findUserByEmail(existingEmail)).thenReturn(Optional.of(new User("user", existingEmail, "password123")));

        assertThatThrownBy(() -> userService.changeEmail(existingEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь с таким email уже существует");
    }

    @Test
    void changePassword_whenPasswordIsValid_shouldChangePasswordAndCallUpdate() {
        String newPassword = "NewPassword123";
        String hashedPassword = PasswordHasher.hash(newPassword);

        userService.changePassword(newPassword);

        assertThat(currentUser.getPassword()).isEqualTo(hashedPassword);
        verify(userRepository, times(1)).update(any(User.class));
    }

    @Test
    void changePassword_whenPasswordIsNullOrBlank_shouldThrowException() {
        assertThatThrownBy(() -> userService.changePassword(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Новый пароль не может быть пустым");

        assertThatThrownBy(() -> userService.changePassword("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Новый пароль не может быть пустым");
    }

    @Test
    void changePassword_whenUserIsNotAuthorized_shouldThrowIllegalStateException() {
        userService.setCurrentUser(null);

        assertThatThrownBy(() -> userService.changePassword("NewPassword123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Пользователь не авторизован");
    }

    @Test
    void changePassword_whenPasswordDoesNotMatchRequirements_shouldThrowIllegalArgumentException() {
        String invalidPassword = "short";

        assertThatThrownBy(() -> userService.changePassword(invalidPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Новый пароль не соответствует требованиям");
    }

    @Test
    void deleteProfile_whenPasswordIsValid_shouldDeleteProfile() {
        String password = "password123";

        userService.deleteProfile(password);

        verify(userRepository, times(1)).deleteById(currentUser.getId());
    }

    @Test
    void deleteProfile_whenPasswordIsNullOrBlank_shouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> userService.deleteProfile(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пароль не может быть пустым");

        assertThatThrownBy(() -> userService.deleteProfile("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пароль не может быть пустым");
    }

    @Test
    void deleteProfile_whenUserIsNotAuthorized_shouldThrowIllegalStateException() {
        userService.setCurrentUser(null);

        assertThatThrownBy(() -> userService.deleteProfile("Password123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Пользователь не авторизован");
    }

    @Test
    void deleteProfile_whenPasswordDoesNotMatch_shouldNotDeleteProfile() {
        String password = "WrongPassword";

        userService.deleteProfile(password);

        verify(userRepository, never()).deleteById(currentUser.getId());
    }
}
