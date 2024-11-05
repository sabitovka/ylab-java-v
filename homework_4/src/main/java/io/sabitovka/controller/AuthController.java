package io.sabitovka.controller;

import io.sabitovka.dto.SuccessResponse;
import io.sabitovka.dto.user.CreateUserDto;
import io.sabitovka.dto.user.UserInfoDto;
import io.sabitovka.dto.user.UserLoginDto;
import io.sabitovka.service.AuthorizationService;
import io.sabitovka.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для обработки авторизации
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthorizationService authService;

    @Operation(summary = "123")
    @PostMapping("/register")
    public SuccessResponse<UserInfoDto> register(@RequestBody CreateUserDto createUserDto) {
        UserInfoDto createdUser = userService.createUser(createUserDto);
        return new SuccessResponse<>(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto loginDto) {
        String token = authService.login(loginDto);

        return ResponseEntity.ok(new SuccessResponse<>(token));
    }
}
