package io.sabitovka.controller;

import io.sabitovka.auth.annotation.RequiresAuthorization;
import io.sabitovka.dto.user.UserInfoDto;
import io.sabitovka.factory.ServiceFactory;
import io.sabitovka.service.UserService;
import io.sabitovka.servlet.RestController;
import io.sabitovka.servlet.annotation.GetMapping;
import io.sabitovka.servlet.annotation.RequestMapping;
import io.sabitovka.servlet.util.SuccessResponse;

import java.util.List;

@RequestMapping("/users")
public class UsersRestController implements RestController {
    private final UserService userService = ServiceFactory.getInstance().getUserService();

    @GetMapping("/{id|\\d+}")
    @RequiresAuthorization
    public UserInfoDto getUserById(String id) {
        return userService.findById(Long.parseLong(id));
    }

    @GetMapping("/blocked")
    @RequiresAuthorization(onlyAdmin = true)
    public SuccessResponse<List<UserInfoDto>> getBlockedUsers() {
        return new SuccessResponse<>(userService.getBlockedUsers());
    }

    @GetMapping("/active")
    @RequiresAuthorization(onlyAdmin = true)
    public SuccessResponse<List<UserInfoDto>> getActiveUsers() {
        return new SuccessResponse<>(userService.getActiveUsers());
    }
}
