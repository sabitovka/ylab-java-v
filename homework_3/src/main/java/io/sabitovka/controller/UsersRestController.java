package io.sabitovka.controller;

import io.sabitovka.auth.annotation.RequiresAuthorization;
import io.sabitovka.dto.user.ChangePasswordDto;
import io.sabitovka.dto.user.UpdateUserDto;
import io.sabitovka.dto.user.UserInfoDto;
import io.sabitovka.factory.ServiceFactory;
import io.sabitovka.service.UserService;
import io.sabitovka.servlet.RestController;
import io.sabitovka.servlet.annotation.*;
import io.sabitovka.servlet.util.SuccessResponse;

import java.util.List;

/**
 * REST-контроллер для управления пользователями
 */
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

    @PutMapping("/{id|\\d+}")
    public SuccessResponse<String> updateUser(UpdateUserDto userInfoDto, String userId) {
        userService.updateUser(Long.valueOf(userId), userInfoDto);
        return new SuccessResponse<>("Пользователь обновлен");
    }

    @GetMapping("/active")
    @RequiresAuthorization(onlyAdmin = true)
    public SuccessResponse<List<UserInfoDto>> getActiveUsers() {
        return new SuccessResponse<>(userService.getActiveUsers());
    }

    @PostMapping("/{id|\\d+}/password")
    @RequiresAuthorization
    public SuccessResponse<String> changePassword(ChangePasswordDto changePasswordDto, String userId) {
        userService.changePassword(Long.valueOf(userId), changePasswordDto);
        return new SuccessResponse<>("Пароль успешно изменен");
    }

    @DeleteMapping("/{id|\\d+}")
    @RequiresAuthorization
    public SuccessResponse<String> deleteProfile(String id) {
        userService.deleteProfile(Long.valueOf(id));
        return new SuccessResponse<>("Профиль удален.");
    }

    @PostMapping("/{id|\\d+}/block")
    @RequiresAuthorization(onlyAdmin = true)
    public SuccessResponse<String> blockUser(String id) {
        userService.blockUser(Long.parseLong(id));
        return new SuccessResponse<>("Пользователь заблокирован");
    }
}
