package io.sabitovka.controller;

import io.sabitovka.dto.user.CreateUserDto;
import io.sabitovka.dto.user.UserInfoDto;
import io.sabitovka.dto.user.UserLoginDto;
import io.sabitovka.factory.ServiceFactory;
import io.sabitovka.service.AuthorizationService;
import io.sabitovka.service.UserService;
import io.sabitovka.servlet.RestController;
import io.sabitovka.servlet.annotation.PostMapping;
import io.sabitovka.servlet.annotation.RequestMapping;
import io.sabitovka.servlet.util.SuccessResponse;

@RequestMapping("/auth")
public class AuthController implements RestController {
    private final UserService userService = ServiceFactory.getInstance().getUserService();
    private final AuthorizationService authService = ServiceFactory.getInstance().getAuthorizationService();

    @PostMapping("/register")
    public SuccessResponse<UserInfoDto> register(CreateUserDto createUserDto) {
        UserInfoDto createdUser = userService.createUser(createUserDto);
        return new SuccessResponse<>(createdUser);
    }

    @PostMapping("/login")
    public SuccessResponse<String> login(UserLoginDto loginDto) {
        String token = authService.login(loginDto.getEmail(), loginDto.getPassword());

        return new SuccessResponse<>(token);
    }
}
