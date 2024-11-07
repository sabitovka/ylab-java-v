package io.sabitovka.habittracker.controller;

import io.sabitovka.habittracker.annotation.RequiresAuthorization;
import io.sabitovka.habittracker.dto.SuccessResponse;
import io.sabitovka.habittracker.dto.user.ChangePasswordDto;
import io.sabitovka.habittracker.dto.user.UpdateUserDto;
import io.sabitovka.habittracker.dto.user.UserInfoDto;
import io.sabitovka.habittracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public SuccessResponse<?> getUserById(@PathVariable("id") Long id) {
        UserInfoDto infoDto = userService.findById(id);
        return new SuccessResponse<>(infoDto);
    }

    @GetMapping("/blocked")
    @RequiresAuthorization(onlyAdmin = true)
    public ResponseEntity<?> getBlockedUsers() {
        return ResponseEntity.ok(new SuccessResponse<>(userService.getBlockedUsers()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(UpdateUserDto userInfoDto, @PathVariable("id") Long id) {
        userService.updateUser(id, userInfoDto);
        return ResponseEntity.ok(new SuccessResponse<>("Пользователь обновлен"));
    }

    @GetMapping("/active")
    @RequiresAuthorization(onlyAdmin = true)
    public ResponseEntity<?> getActiveUsers() {
        return ResponseEntity.ok(new SuccessResponse<>(userService.getActiveUsers()));
    }

    @PostMapping("/{id}/password")
    @RequiresAuthorization
    public ResponseEntity<?> changePassword(ChangePasswordDto changePasswordDto, @PathVariable("id") Long id) {
        userService.changePassword(id, changePasswordDto);
        return ResponseEntity.ok(new SuccessResponse<>("Пароль успешно изменен"));
    }

    @DeleteMapping("/{id}")
    @RequiresAuthorization
    public ResponseEntity<?> deleteProfile(@PathVariable("id") Long id) {
        userService.deleteProfile(id);
        return ResponseEntity.ok(new SuccessResponse<>("Профиль удален."));
    }

    @PostMapping("/{id}/block")
    @RequiresAuthorization(onlyAdmin = true)
    public ResponseEntity<?> blockUser(@PathVariable("id") Long id) {
        userService.blockUser(id);
        return ResponseEntity.ok(new SuccessResponse<>("Пользователь заблокирован"));
    }
}
