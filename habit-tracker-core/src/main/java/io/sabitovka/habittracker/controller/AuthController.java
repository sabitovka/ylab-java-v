package io.sabitovka.habittracker.controller;

import io.sabitovka.auditlogging.annotation.Loggable;
import io.sabitovka.habittracker.dto.ErrorDto;
import io.sabitovka.habittracker.dto.SuccessResponse;
import io.sabitovka.habittracker.dto.user.CreateUserDto;
import io.sabitovka.habittracker.dto.user.UserInfoDto;
import io.sabitovka.habittracker.dto.user.UserLoginDto;
import io.sabitovka.habittracker.service.AuthorizationService;
import io.sabitovka.habittracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для обработки авторизации
 */
@Tag(name = "Авторизация", description = "Сервис для авторизации")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Loggable
public class AuthController {
    private final UserService userService;
    private final AuthorizationService authService;

    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<SuccessResponse<UserInfoDto>> register(@RequestBody CreateUserDto createUserDto) {
        UserInfoDto createdUser = userService.createUser(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse<>(createdUser));
    }

    @Operation(summary = "Авторизация пользователя в системе")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Токен авторизации"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<String>> login(@RequestBody UserLoginDto loginDto) {
        String token = authService.login(loginDto);
        return ResponseEntity.ok(new SuccessResponse<>(token));
    }
}
