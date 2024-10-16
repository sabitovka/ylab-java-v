package io.sabitovka.service;

import io.sabitovka.dto.UserInfoDto;
import io.sabitovka.enums.HabitFrequency;
import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.util.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private HabitRepository habitRepository;
    @Mock
    private AuthorizationService authorizationService;
    @InjectMocks
    private UserService userService;

    private User currentUser;

    @BeforeEach
    public void setUp() {
        String password = PasswordHasher.hash("password123");
        currentUser = new User(1L, "user", "user@example.com", password, false, true);
        lenient().when(authorizationService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    public void mapUserToUserInfoDto_shouldReturnCorrectObj() {
        User user = new User(1L, "mock", "mock@example.com", "password", false, true);
        UserInfoDto userInfoDto = userService.mapUserToUserInfoDto(user);

        assertThat(userInfoDto.getId()).isEqualTo(user.getId());
        assertThat(userInfoDto.getName()).isEqualTo(user.getName());
        assertThat(userInfoDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(userInfoDto.getPassword()).isEqualTo(user.getPassword());
        assertThat(userInfoDto.isAdmin()).isEqualTo(user.isAdmin());
        assertThat(userInfoDto.isActive()).isEqualTo(user.isActive());
    }

    @Test
    public void mapUserInfoDtoToUser_shouldReturnCorrectObj() {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setId(1L);
        userInfoDto.setName("mock");
        userInfoDto.setEmail("mock@example.com");
        userInfoDto.setPassword("password");
        userInfoDto.setIsAdmin(false);
        userInfoDto.setActive(true);

        User user = userService.mapUserInfoDtoToUser(userInfoDto);

        assertThat(user.getId()).isEqualTo(userInfoDto.getId());
        assertThat(user.getName()).isEqualTo(userInfoDto.getName());
        assertThat(user.getEmail()).isEqualTo(userInfoDto.getEmail());
        assertThat(user.getPassword()).isEqualTo(userInfoDto.getPassword());
        assertThat(user.isAdmin()).isEqualTo(userInfoDto.isAdmin());
        assertThat(user.isActive()).isEqualTo(userInfoDto.isActive());
    }

    @Test
    public void createUser_whenRegistrationInputIncorrect_shouldThrowException() {
        String name = "mock";
        String email = "mock@example.com";
        String password = "password";

        UserInfoDto userInfoDto1 = new UserInfoDto();
        userInfoDto1.setName(" ");
        userInfoDto1.setEmail(email);
        userInfoDto1.setPassword(password);

        UserInfoDto userInfoDto2 = new UserInfoDto();
        userInfoDto2.setName(name);
        userInfoDto2.setEmail("sssdd");
        userInfoDto2.setPassword(password);

        UserInfoDto userInfoDto3 = new UserInfoDto();
        userInfoDto3.setName(name);
        userInfoDto3.setEmail(email);
        userInfoDto3.setPassword("123");

        assertThatThrownBy(() -> userService.createUser(userInfoDto1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Имя не может быть пустым");

        assertThatThrownBy(() -> userService.createUser(userInfoDto2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Неправильный формат email");

        assertThatThrownBy(() -> userService.createUser(userInfoDto3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пароль не соответствует требованиям");
    }

    @Test
    public void createUser_whenEmailAlreadyExists_shouldThrowException() {
        String email = "mock@example.com";
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(new User()));
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setName("mock");
        userInfoDto.setEmail(email);
        userInfoDto.setPassword("password");

        assertThatThrownBy(() -> userService.createUser(userInfoDto))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("Данный email уже занят");
        verify(userRepository, times(0)).create(any(User.class));
    }

    @Test
    void createUser_whenUserIsCorrect_shouldCreateSuccessfully() {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setName("mock");
        userInfoDto.setEmail("mock@example.com");
        userInfoDto.setPassword("password");

        when(userRepository.create(any(User.class))).thenReturn(any(User.class));

        userService.createUser(userInfoDto);

        verify(userRepository, times(1)).create(any(User.class));
    }

    @Test
    void updateUser_whenUserInfoIsNull_shouldThrowException() {
        assertThatThrownBy(() -> userService.updateUser(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateUser_whenUserInfoIsCorrect_shouldCreateSuccessfully() {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setId(1L);
        userInfoDto.setName("updatedMock");
        userInfoDto.setEmail("updatedMock@example.com");
        userInfoDto.setPassword("password");
        when(userRepository.update(any(User.class))).thenReturn(true);

        userService.updateUser(userInfoDto);

        verify(userRepository).update(any(User.class));
    }

    @Test
    void changePassword_shouldChangeSuccessfully() {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setId(1L);
        userInfoDto.setName("mock");
        userInfoDto.setEmail("mock@example.com");
        userInfoDto.setPassword("newPassword123");

        User existingUser = new User(1L, "mock", "mock@example.com", PasswordHasher.hash("oldPassword123"), false, true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.update(any(User.class))).thenReturn(true);

        userService.changePassword(userInfoDto, "oldPassword123");

        verify(userRepository).update(any(User.class));
    }

    @Test
    void changePassword_whenOldPasswordIsIncorrect_shouldThrowException() {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setId(1L);
        userInfoDto.setName("mock");
        userInfoDto.setEmail("mock@example.com");
        userInfoDto.setPassword("newPassword123");

        User existingUser = new User(1L, "mock", "mock@example.com", PasswordHasher.hash("oldPassword123"), false, true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.changePassword(userInfoDto, "wrongOldPassword"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void changePassword_shouldThrowExceptionWhenValidationFails() {
        UserInfoDto userInfoDto = new UserInfoDto();

        assertThatThrownBy(() -> userService.changePassword(userInfoDto, "oldPassword123"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteProfile_whenPasswordDoesNotMatch_shouldThrowException() {
        assertThatThrownBy(() -> userService.deleteProfile(1L, "123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пароль не соответствует требованиям");
    }

    @Test
    void deleteProfile_whenPasswordIsNotEqualToStoredPassword_shouldDoNothing() {
        User user = new User(1L, "mock", "mock@example.ru", "password", false, true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.deleteProfile(user.getId(), "wrongPassword");

        verify(userRepository, times(0)).deleteById(anyLong());
        verify(habitRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void deleteProfile_whenPasswordCorrect_shouldDeleteUserAndHabits() {
        User user1 = new User(1L, "mock", "mock@example.ru", PasswordHasher.hash("password"), false, true);

        Habit habit1 = new Habit(1L, "name", "description", HabitFrequency.DAILY, 1L);
        Habit habit2 = new Habit(2L, "name", "description", HabitFrequency.DAILY, 1L);

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(habitRepository.findAllByUser(user1)).thenReturn(List.of(habit1, habit2));
        when(habitRepository.findAllByUser(user1)).thenReturn(List.of(habit1, habit2));

        userService.deleteProfile(user1.getId(), "password");

        verify(userRepository).deleteById(user1.getId());
        verify(habitRepository).deleteById(habit1.getId());
        verify(habitRepository).deleteById(habit2.getId());
    }

    @Test
    void blockUser_whenUserExists_shouldBlockUser() {
        User user = new User(1L, "mock", "mock@example.ru", "password", false, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.blockUser(1L);

        assertThat(user.isActive()).isFalse();
        verify(userRepository).update(user);
    }

    @Test
    void findById_whenUserExists_shouldReturnUserInfoDto() {
        User user = new User(1L, "mock", "mock@example.ru", "password", false, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserInfoDto result = userService.findById(1L);

        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getName()).isEqualTo(user.getName());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_whenUserDoesNotExist_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class);
        verify(userRepository).findById(1L);
    }

    @Test
    void getBlockedUsers_shouldReturnOnlyBlockedUsers() {
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
    void getActiveUsers_shouldReturnOnlyActiveUsers() {
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
