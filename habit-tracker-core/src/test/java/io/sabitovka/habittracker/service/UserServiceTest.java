package io.sabitovka.habittracker.service;

import io.sabitovka.habittracker.auth.AuthInMemoryContext;
import io.sabitovka.habittracker.auth.entity.UserDetails;
import io.sabitovka.habittracker.auth.util.PasswordHasher;
import io.sabitovka.habittracker.dto.user.ChangePasswordDto;
import io.sabitovka.habittracker.dto.user.CreateUserDto;
import io.sabitovka.habittracker.dto.user.UpdateUserDto;
import io.sabitovka.habittracker.dto.user.UserInfoDto;
import io.sabitovka.habittracker.enums.HabitFrequency;
import io.sabitovka.habittracker.exception.ApplicationException;
import io.sabitovka.habittracker.exception.ValidationException;
import io.sabitovka.habittracker.model.Habit;
import io.sabitovka.habittracker.model.User;
import io.sabitovka.habittracker.repository.FulfilledHabitRepository;
import io.sabitovka.habittracker.repository.HabitRepository;
import io.sabitovka.habittracker.repository.UserRepository;
import io.sabitovka.habittracker.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("Тест userServiceImpl")
@ExtendWith({ MockitoExtension.class })
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private FulfilledHabitRepository fulfilledHabitRepository;

    @Mock
    private AuthInMemoryContext authInMemoryContext;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private UserServiceImpl userService;

    UserDetails adminUserDetails = new UserDetails(1L, "admin", true);
    UserDetails simpleUserDetails = new UserDetails(2L, "user", false);

    @Test
    @DisplayName("[createUser] Когда регистрационные данные неверные, должен выбросить исключение")
    public void createUserWhenRegistrationInputIncorrectShouldThrowException() {
        String name = "mock";
        String email = "mock@example.com";
        String password = "password";

        CreateUserDto createUserDto1 = new CreateUserDto();
        createUserDto1.setName(" ");
        createUserDto1.setEmail(email);
        createUserDto1.setPassword(password);

        CreateUserDto userInfoDto2 = new CreateUserDto();
        userInfoDto2.setName(name);
        userInfoDto2.setEmail("sssdd");
        userInfoDto2.setPassword(password);

        CreateUserDto userInfoDto3 = new CreateUserDto();
        userInfoDto3.setName(name);
        userInfoDto3.setEmail(email);
        userInfoDto3.setPassword("123");

        assertThatThrownBy(() -> userService.createUser(createUserDto1))
                .isInstanceOf(ValidationException.class);

        assertThatThrownBy(() -> userService.createUser(userInfoDto2))
                .isInstanceOf(ValidationException.class);

        assertThatThrownBy(() -> userService.createUser(userInfoDto3))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("[createUser] Когда email же есть, выбросить исключение")
    public void createUserWhenEmailAlreadyExistsShouldThrowException() {
        String email = "mock@example.com";
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(new User()));
        CreateUserDto userInfoDto = new CreateUserDto();
        userInfoDto.setName("mock");
        userInfoDto.setEmail(email);
        userInfoDto.setPassword("password");

        assertThatThrownBy(() -> userService.createUser(userInfoDto))
                .isInstanceOf(ApplicationException.class);
        verify(userRepository, times(0)).create(any(User.class));
    }

    @Test
    @DisplayName("[createUser] Должен создать пользователя")
    void createUserWhenUserIsCorrectShouldCreateSuccessfully() {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setName("mock");
        createUserDto.setEmail("mock@example.com");
        createUserDto.setPassword("password");

        when(userRepository.create(any(User.class))).thenReturn(any(User.class));

        userService.createUser(createUserDto);

        verify(userRepository, times(1)).create(any(User.class));
    }

    @Test
    @DisplayName("[updateUser] Должен обновить от имени пользователя и от имени администратора")
    void updateUserWhenUserInfoIsCorrectShouldCreateSuccessfully() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName("updatedMock");
        updateUserDto.setEmail("updatedMock@example.com");

        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(userRepository.update(any(User.class))).thenReturn(true);

        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);

            when(authInMemoryContext.getAuthentication()).thenReturn(adminUserDetails);
            userService.updateUser(2L, updateUserDto);

            when(authInMemoryContext.getAuthentication()).thenReturn(simpleUserDetails);
            userService.updateUser(2L, updateUserDto);

            verify(userRepository, times(2)).update(any(User.class));
        }
    }

    @Test
    @DisplayName("[changePassword] Должен обновить пароль от имени пользователя и администратора")
    void changePasswordShouldChangeSuccessfully() {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setNewPassword("newPassword");
        changePasswordDto.setOldPassword("oldPassword123");

        User existingUser = new User(2L, "mock", "mock@example.com", "oldPassword123", false, true);
        when(userRepository.findById(2L)).thenReturn(Optional.of(existingUser));

        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(authInMemoryContext.getAuthentication()).thenReturn(adminUserDetails);

            when(passwordHasher.verify(changePasswordDto.getOldPassword(), "oldPassword123")).thenReturn(true);
            userService.changePassword(2L, changePasswordDto);

            User existingUser1 = new User(2L, "mock", "mock@example.com", "oldPassword123", false, true);
            when(userRepository.findById(2L)).thenReturn(Optional.of(existingUser1));

            when(authInMemoryContext.getAuthentication()).thenReturn(simpleUserDetails);
            when(passwordHasher.verify(changePasswordDto.getOldPassword(), "oldPassword123")).thenReturn(true);
            userService.changePassword(2L, changePasswordDto);

            verify(userRepository, times(2)).update(any(User.class));
        }
    }

    @Test
    @DisplayName("[changePassword] Должен выбросить исключение, когда пароли не совпадают")
    void changePasswordWhenOldPasswordIsIncorrectShouldThrowException() {
        User existingUser = new User(2L, "mock", "mock@example.com", passwordHasher.hash("oldPassword123"), false, true);
        when(userRepository.findById(2L)).thenReturn(Optional.of(existingUser));

        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);

            ChangePasswordDto changePasswordDto = new ChangePasswordDto();
            changePasswordDto.setNewPassword("newPassword");
            changePasswordDto.setOldPassword("wrongOldPassword");

            when(authInMemoryContext.getAuthentication()).thenReturn(simpleUserDetails);

            assertThatThrownBy(() -> userService.changePassword(2L, changePasswordDto))
                    .isInstanceOf(ApplicationException.class);
        }

    }

    @Test
    @DisplayName("[changePassword] Должен выбросить исключение при валидации")
    void changePasswordShouldThrowExceptionWhenValidationFails() {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setNewPassword("");
        changePasswordDto.setOldPassword("");

        assertThatThrownBy(() -> userService.changePassword(1L, changePasswordDto))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("[deleteProfile] Удалить пользователя и привычки")
    void deleteProfileShouldDeleteUserAndHabits() {
        User user1 = new User(2L, "mock", "mock@example.ru", passwordHasher.hash("password"), false, true);
        Habit habit1 = new Habit(1L, "name", "description", HabitFrequency.DAILY, LocalDate.now(), true, 2L);
        Habit habit2 = new Habit(2L, "name", "description", HabitFrequency.DAILY, LocalDate.now(), true, 2L);

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(habitRepository.findAllByUserId(user1.getId())).thenReturn(List.of(habit1, habit2));
        when(fulfilledHabitRepository.findAllByHabit(habit1)).thenReturn(Collections.emptyList());

        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);

            when(authInMemoryContext.getAuthentication()).thenReturn(simpleUserDetails);
            userService.deleteProfile(user1.getId());

            verify(userRepository).deleteById(user1.getId());
            verify(habitRepository).deleteById(habit1.getId());
            verify(habitRepository).deleteById(habit2.getId());
        }

    }

    @Test
    @DisplayName("[blockUser] Должен заблокировать пользователя")
    void blockUserWhenUserExists_shouldBlockUser() {
        User user = new User(1L, "mock", "mock@example.ru", "password", true, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);

            when(authInMemoryContext.getAuthentication()).thenReturn(adminUserDetails);
            userService.blockUser(1L);

            assertThat(user.isActive()).isFalse();
            verify(userRepository).update(user);
        }
    }

    @Test
    @DisplayName("[findById] Должен вернуть инфо")
    void findByIdWhenUserExistsShouldReturnUserInfoDto() {
        User user = new User(1L, "mock", "mock@example.ru", "password", false, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserInfoDto result = userService.findById(1L);

        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getName()).isEqualTo(user.getName());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("[findById] Должен выбросить исключение, т.к. пользователь не найден")
    void findByIdWhenUserDoesNotExistShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(1L))
                .isInstanceOf(ApplicationException.class);
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("[getBlockedUsers] Должен вернуть только заблокированных пользователей")
    void getBlockedUsersShouldReturnOnlyBlockedUsers() {
        User activeUser = new User(1L, "Active", "active@example.ru", "password", false, true);
        User blockedUser1 = new User(2L, "Blocked1", "blocked1@example.ru", "password", false, false);
        User blockedUser2 = new User(3L, "Blocked2", "blocked2@example.ru", "password", false, false);
        when(userRepository.findAll()).thenReturn(List.of(activeUser, blockedUser1, blockedUser2));

        List<UserInfoDto> blockedUsers = userService.getBlockedUsers();

        assertThat(blockedUsers).hasSize(2);
        assertThat(blockedUsers).extracting(UserInfoDto::getId).containsExactly(2L, 3L);
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("[getActiveUsers] Должен вернуть только активных пользователей")
    void getActiveUsersShouldReturnOnlyActiveUsers() {
        User activeUser1 = new User(1L, "Active1", "active1@example.ru", "password", false, true);
        User activeUser2 = new User(2L, "Active2", "active2@example.ru", "password", false, true);
        User blockedUser = new User(3L, "Blocked", "blocked@example.ru", "password", false, false);
        when(userRepository.findAll()).thenReturn(List.of(activeUser1, activeUser2, blockedUser));

        List<UserInfoDto> activeUsers = userService.getActiveUsers();

        assertThat(activeUsers).hasSize(2);
        assertThat(activeUsers).extracting(UserInfoDto::getId).containsExactly(1L, 2L);
        verify(userRepository).findAll();
    }
}
