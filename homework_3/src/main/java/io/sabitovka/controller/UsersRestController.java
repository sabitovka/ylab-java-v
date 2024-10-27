package io.sabitovka.controller;

import io.sabitovka.dto.CreateUserDto;
import io.sabitovka.dto.UserInfoDto;
import io.sabitovka.factory.ServiceFactory;
import io.sabitovka.service.UserService;
import io.sabitovka.servlet.RestController;
import io.sabitovka.servlet.annotation.GetMapping;
import io.sabitovka.servlet.annotation.PostMapping;
import io.sabitovka.servlet.annotation.RequestBody;
import io.sabitovka.servlet.annotation.RequestMapping;
import io.sabitovka.servlet.util.SuccessResponse;
import io.sabitovka.util.validation.Validator;

import java.util.List;

@RequestMapping("/users")
public class UsersRestController implements RestController {
    private final UserService userService = ServiceFactory.getInstance().getUserService();

    @GetMapping("/{id|\\d+}")
    public UserInfoDto getUserById(String id) {
        return userService.findById(Long.parseLong(id));
    }

    @GetMapping("/blocked")
    public SuccessResponse<List<UserInfoDto>> getBlockedUsers() {
        return new SuccessResponse<>(userService.getBlockedUsers());
    }

    @GetMapping("/active")
    public SuccessResponse<List<UserInfoDto>> getActiveUsers() {
        return new SuccessResponse<>(userService.getActiveUsers());
    }

    @PostMapping("/")
    public CreateUserDto createUser(@RequestBody CreateUserDto createUserDto) {
        Validator.validate(createUserDto);
        return createUserDto;
    }
}
