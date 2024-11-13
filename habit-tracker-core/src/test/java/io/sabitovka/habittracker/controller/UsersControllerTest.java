package io.sabitovka.habittracker.controller;

import io.sabitovka.habittracker.dto.user.ChangePasswordDto;
import io.sabitovka.habittracker.dto.user.UpdateUserDto;
import io.sabitovka.habittracker.dto.user.UserInfoDto;
import io.sabitovka.habittracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UsersController usersController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new UsersController(userService)).build();
    }

    @Test
    @DisplayName("GET /api/users/{id} - Получить пользователя по ID")
    void shouldReturnUserInfoById() throws Exception {
        Long userId = 1L;
        UserInfoDto userInfo = new UserInfoDto(userId, "user1", "user1@example.com");

        when(userService.findById(userId)).thenReturn(userInfo);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.name").value("user1"))
                .andExpect(jsonPath("$.data.email").value("user1@example.com"));
    }

    @Test
    @DisplayName("GET /api/users/blocked - Получить список заблокированных пользователей")
    void shouldReturnBlockedUsers() throws Exception {
        UserInfoDto blockedUser = new UserInfoDto(1L, "Blocked User", "blocked@example.com");
        List<UserInfoDto> blockedUsers = List.of(blockedUser);

        when(userService.getBlockedUsers()).thenReturn(blockedUsers);

        mockMvc.perform(get("/api/users/blocked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Blocked User"))
                .andExpect(jsonPath("$.data[0].email").value("blocked@example.com"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Обновить данные пользователя")
    void shouldUpdateUser() throws Exception {
        Long userId = 1L;
        UpdateUserDto updateUserDto = new UpdateUserDto("Updated Name", "updated@example.com");

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Пользователь обновлен"));
    }

    @Test
    @DisplayName("GET /api/users/active - Получить список активных пользователей")
    void shouldReturnActiveUsers() throws Exception {
        UserInfoDto activeUser = new UserInfoDto(1L, "Active User", "active@example.com");
        List<UserInfoDto> activeUsers = Collections.singletonList(activeUser);

        when(userService.getActiveUsers()).thenReturn(activeUsers);

        mockMvc.perform(get("/api/users/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Active User"))
                .andExpect(jsonPath("$.data[0].email").value("active@example.com"));
    }

    @Test
    @DisplayName("POST /api/users/{id}/password - Изменить пароль пользователя")
    void shouldChangePassword() throws Exception {
        Long userId = 1L;
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("oldPassword", "newPassword");

        mockMvc.perform(post("/api/users/{id}/password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Пароль успешно изменен"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Удалить профиль пользователя")
    void shouldDeleteUserProfile() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Профиль удален."));

        verify(userService, times(1)).deleteProfile(userId);
    }

    @Test
    @DisplayName("POST /api/users/{id}/block - Заблокировать пользователя")
    void shouldBlockUser() throws Exception {
        Long userId = 1L;

        mockMvc.perform(post("/api/users/{id}/block", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Пользователь заблокирован"));

        verify(userService, times(1)).blockUser(userId);
    }
}
