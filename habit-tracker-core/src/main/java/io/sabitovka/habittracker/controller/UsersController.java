package io.sabitovka.habittracker.controller;

import io.sabitovka.habittracker.auth.annotation.RequiresAuthorization;
import io.sabitovka.habittracker.common.Constants;
import io.sabitovka.habittracker.dto.ErrorDto;
import io.sabitovka.habittracker.dto.SuccessResponse;
import io.sabitovka.habittracker.dto.user.ChangePasswordDto;
import io.sabitovka.habittracker.dto.user.UpdateUserDto;
import io.sabitovka.habittracker.dto.user.UserInfoDto;
import io.sabitovka.habittracker.service.UserService;
import io.sabitovka.logging.annotation.Loggable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-контроллер для управления пользователями
 */
@Tag(name = "Пользователи", description = "Управление пользователями в системе")
@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
@Loggable
public class UsersController {
    private final UserService userService;

    @Operation(summary = "Получить пользователя по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные пользователя"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @GetMapping("/{id}")
    @RequiresAuthorization
    public ResponseEntity<SuccessResponse<UserInfoDto>> getUserById(@PathVariable("id") Long id) {
        UserInfoDto infoDto = userService.findById(id);
        return ResponseEntity.ok(new SuccessResponse<>(infoDto));
    }

    @Operation(summary = "Получить заблокированных пользователей")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список заблокированных пользователей"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @GetMapping("/blocked")
    @RequiresAuthorization(onlyAdmin = true)
    public ResponseEntity<SuccessResponse<List<UserInfoDto>>> getBlockedUsers() {
        return ResponseEntity.ok(new SuccessResponse<>(userService.getBlockedUsers()));

    }

    @Operation(summary = "Обновить пользователя по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь обновлен"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @PutMapping("/{id}")
    @RequiresAuthorization
    public ResponseEntity<Void> updateUser(UpdateUserDto userInfoDto, @PathVariable("id") Long id) {
        userService.updateUser(id, userInfoDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить активных пользователей")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список пользователей"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @GetMapping("/active")
    @RequiresAuthorization(onlyAdmin = true)
    public ResponseEntity<SuccessResponse<List<UserInfoDto>>> getActiveUsers() {
        return ResponseEntity.ok(new SuccessResponse<>(userService.getActiveUsers()));
    }

    @Operation(summary = "Смена пароля пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь успешно обновлен"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @PostMapping("/{id}/password")
    @RequiresAuthorization
    public ResponseEntity<Void> changePassword(ChangePasswordDto changePasswordDto, @PathVariable("id") Long id) {
        userService.changePassword(id, changePasswordDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить профиль со всеми привычками")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удален"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @DeleteMapping("/{id}")
    @RequiresAuthorization
    public ResponseEntity<Void> deleteProfile(@PathVariable("id") Long id) {
        userService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Заблокировать пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь заблокирован"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @PostMapping("/{id}/block")
    @RequiresAuthorization(onlyAdmin = true)
    public ResponseEntity<Void> blockUser(@PathVariable("id") Long id) {
        userService.blockUser(id);
        return ResponseEntity.noContent().build();
    }
}
