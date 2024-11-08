package io.sabitovka.habittracker.controller;

import io.sabitovka.auditlogging.annotation.Loggable;
import io.sabitovka.habittracker.dto.SuccessResponse;
import io.sabitovka.habittracker.dto.user.CreateUserDto;
import io.sabitovka.habittracker.dto.user.UserInfoDto;
import io.sabitovka.habittracker.dto.user.UserLoginDto;
import io.sabitovka.habittracker.service.AuthorizationService;
import io.sabitovka.habittracker.service.UserService;
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
@Loggable
public class AuthController {
    private final UserService userService;
    private final AuthorizationService authService;

    @PostMapping("/register")
    public SuccessResponse<?> register(@RequestBody CreateUserDto createUserDto) {
        UserInfoDto createdUser = userService.createUser(createUserDto);
        return new SuccessResponse<>(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto loginDto) {
        String token = authService.login(loginDto);

        return ResponseEntity.ok(new SuccessResponse<>(token));
    }
}
