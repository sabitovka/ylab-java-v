package io.sabitovka.controller;

import io.sabitovka.auth.annotation.RequiresAuthorization;
import io.sabitovka.dto.SuccessResponse;
import io.sabitovka.dto.user.ChangePasswordDto;
import io.sabitovka.dto.user.UpdateUserDto;
import io.sabitovka.dto.user.UserInfoDto;
import io.sabitovka.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления пользователями
 */
@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;

    @GetMapping("/{id}")
    @RequiresAuthorization
    public UserInfoDto getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/blocked")
    @RequiresAuthorization(onlyAdmin = true)
    public SuccessResponse<List<UserInfoDto>> getBlockedUsers() {
        return new SuccessResponse<>(userService.getBlockedUsers());
    }

    @PutMapping("/{id}")
    public SuccessResponse<String> updateUser(UpdateUserDto userInfoDto, @PathVariable Long id) {
        userService.updateUser(id, userInfoDto);
        return new SuccessResponse<>("Пользователь обновлен");
    }

    @GetMapping("/active")
    @RequiresAuthorization(onlyAdmin = true)
    public SuccessResponse<List<UserInfoDto>> getActiveUsers() {
        return new SuccessResponse<>(userService.getActiveUsers());
    }

    @PostMapping("/{id}/password")
    @RequiresAuthorization
    public SuccessResponse<String> changePassword(ChangePasswordDto changePasswordDto, @PathVariable Long id) {
        userService.changePassword(id, changePasswordDto);
        return new SuccessResponse<>("Пароль успешно изменен");
    }

    @DeleteMapping("/{id}")
    @RequiresAuthorization
    public SuccessResponse<String> deleteProfile(@PathVariable Long id) {
        userService.deleteProfile(id);
        return new SuccessResponse<>("Профиль удален.");
    }

    @PostMapping("/{id}/block")
    @RequiresAuthorization(onlyAdmin = true)
    public SuccessResponse<String> blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return new SuccessResponse<>("Пользователь заблокирован");
    }
}
