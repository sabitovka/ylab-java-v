package io.sabitovka.controller;

import io.sabitovka.dto.user.CreateUserDto;
import io.sabitovka.dto.user.UserInfoDto;
import io.sabitovka.dto.user.UserLoginDto;
import io.sabitovka.enums.ErrorCode;
import io.sabitovka.exception.ApplicationException;
import io.sabitovka.exception.ValidationException;
import io.sabitovka.service.AuthorizationService;
import io.sabitovka.service.UserService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Тест контроллера AuthController")
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthorizationService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(userService, authService))
                .setControllerAdvice(new ExceptionController())
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/register - Успешная регистрация")
    public void postRegisterSuccess() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto("Test User", "test@example.com", "password");
        UserInfoDto userInfoDto = new UserInfoDto(1L, "test@example.com", "Test User");

        when(userService.createUser(any(CreateUserDto.class))).thenReturn(userInfoDto);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userInfoDto.getId()))
                .andExpect(jsonPath("$.data.email").value(userInfoDto.getEmail()))
                .andExpect(jsonPath("$.data.name").value(userInfoDto.getName()));
    }

    @Test
    @DisplayName("POST /api/auth/register - Email уже занят")
    public void postRegisterWhenEmailAlreadyExistShouldReturnBadRequest() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto("Test User", "test@example.com", "password");

        when(userService.createUser(any(CreateUserDto.class)))
                .thenThrow(new ApplicationException(ErrorCode.BAD_REQUEST, "Данный email уже занят"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Данный email уже занят"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Ошибка валидации Email и Password")
    public void postRegisterWhenEmailOrPasswordNotValidShouldReturnBadRequest() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto("Test User", "test", "12");

        when(userService.createUser(any(CreateUserDto.class)))
                .thenThrow(new ValidationException(List.of("Ошибка ввода")));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - Успешный вход")
    public void postLoginSuccess() throws Exception {
        UserLoginDto userLoginDto = new UserLoginDto("test@example.com", "password");
        String mockToken = "mockJwtToken";

        when(authService.login(any(UserLoginDto.class))).thenReturn(mockToken);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(mockToken));
    }

    @Test
    @DisplayName("POST /api/auth/login - Неверный логин и пароль")
    public void postLoginWhenEmailOrPasswordIncorrectShouldReturnUnauthorized() throws Exception {
        UserLoginDto userLoginDto = new UserLoginDto("test@example.com", "password");
        when(authService.login(any(UserLoginDto.class)))
                .thenThrow(new ApplicationException(ErrorCode.UNAUTHORIZED, "Неверный логин или пароль"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Неверный логин или пароль"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Учетная запись заблокирована")
    public void postLoginProfileWasBlockedShouldReturnForbidden() throws Exception {
        UserLoginDto userLoginDto = new UserLoginDto("test@example.com", "password");
        when(authService.login(any(UserLoginDto.class)))
                .thenThrow(new ApplicationException(ErrorCode.FORBIDDEN, "Ваша учетная запись была заблокирована"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Ваша учетная запись была заблокирована"));
    }
}
