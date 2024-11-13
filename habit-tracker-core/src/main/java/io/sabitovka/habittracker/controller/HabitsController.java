package io.sabitovka.habittracker.controller;

import io.sabitovka.habittracker.auth.annotation.RequiresAuthorization;
import io.sabitovka.habittracker.common.Constants;
import io.sabitovka.habittracker.dto.ErrorDto;
import io.sabitovka.habittracker.dto.SuccessResponse;
import io.sabitovka.habittracker.dto.habit.HabitFilterDto;
import io.sabitovka.habittracker.dto.habit.HabitInfoDto;
import io.sabitovka.habittracker.dto.habit.SimpleLocalDateDto;
import io.sabitovka.habittracker.service.HabitService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-контроллер для управления привычками пользователя
 */
@Tag(name = "Привычки", description = "Управление привычками пользователей в системе")
@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
@Loggable
public class HabitsController {
    private final HabitService habitService;

    @Operation(summary = "Создать новую привычку")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Созданная привычка"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @PostMapping
    @RequiresAuthorization
    public ResponseEntity<SuccessResponse<HabitInfoDto>> createHabit(HabitInfoDto habitInfoDto) {
        HabitInfoDto created = habitService.createHabit(habitInfoDto);
        return ResponseEntity.ok(new SuccessResponse<>(created));
    }

    @Operation(summary = "Обновить привычку")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Привычка обновлена"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @PutMapping("/{id}")
    @RequiresAuthorization
    public ResponseEntity<?> updateHabit(HabitInfoDto habitInfoDto, @PathVariable("id") Long id) {
        habitService.updateHabit(id, habitInfoDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить привычки по фильтру")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список отфильтрованных привычек"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @PostMapping("/filter")
    @RequiresAuthorization
    public ResponseEntity<SuccessResponse<List<HabitInfoDto>>> filterHabitsByFilters(HabitFilterDto filterDto) {
        List<HabitInfoDto> habitsByFilters = habitService.getHabitsByFilters(filterDto);
        return ResponseEntity.ok(new SuccessResponse<>(habitsByFilters));
    }

    @Operation(summary = "Получить привычки пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список привычек пользователя"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @GetMapping("/user/{userId}")
    @RequiresAuthorization
    public ResponseEntity<SuccessResponse<List<HabitInfoDto>>> getUserHabits(@PathVariable("userId") Long userId) {
        List<HabitInfoDto> allByOwner = habitService.getAllByOwner(userId);
        return ResponseEntity.ok(new SuccessResponse<>(allByOwner));
    }

    @Operation(summary = "Отключить привычку")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Привычка отключена"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @PutMapping("/{id}/disable")
    @RequiresAuthorization
    public ResponseEntity<Void> disableHabit(@PathVariable("id") Long id) {
        habitService.disableHabit(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить привычку по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Привычка"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @GetMapping("/{id}")
    @RequiresAuthorization
    public ResponseEntity<SuccessResponse<HabitInfoDto>> getHabitById(@PathVariable("id") Long id) {
        HabitInfoDto habitById = habitService.getHabitById(id);
        return ResponseEntity.ok(new SuccessResponse<>(habitById));
    }

    @Operation(summary = "Удалить привычку со всей историей выполнения")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Привычка удалена"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @DeleteMapping("/{id}")
    @RequiresAuthorization
    public ResponseEntity<Void> deleteHabit(@PathVariable("id") Long id) {
        habitService.delete(id);
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Выполнить привычку")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Привычка выполнена"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @PostMapping("/{id}/fulfill")
    @RequiresAuthorization
    public ResponseEntity<Void> fulfillHabit(@RequestBody SimpleLocalDateDto localDateDto, @PathVariable("id") String id) {
        habitService.markHabitAsFulfilled(Long.valueOf(id), localDateDto);
        return ResponseEntity.noContent().build();
    }
}
