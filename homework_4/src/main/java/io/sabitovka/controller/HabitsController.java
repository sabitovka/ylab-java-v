package io.sabitovka.controller;

import io.sabitovka.annotation.RequiresAuthorization;
import io.sabitovka.dto.SuccessResponse;
import io.sabitovka.dto.habit.HabitFilterDto;
import io.sabitovka.dto.habit.HabitInfoDto;
import io.sabitovka.dto.habit.SimpleLocalDateDto;
import io.sabitovka.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления привычками пользователя
 */
@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitsController {
    private final HabitService habitService;

    @PostMapping
    @RequiresAuthorization
    public SuccessResponse<HabitInfoDto> createHabit(HabitInfoDto habitInfoDto) {
        HabitInfoDto created = habitService.createHabit(habitInfoDto);
        return new SuccessResponse<>(created);
    }

    @PutMapping("/{id}")
    @RequiresAuthorization
    public SuccessResponse<String> updateHabit(HabitInfoDto habitInfoDto, @PathVariable Long id) {
        habitService.updateHabit(id, habitInfoDto);
        return new SuccessResponse<>("Привычка обновлена");
    }

    @PostMapping("/filter")
    @RequiresAuthorization
    public SuccessResponse<List<HabitInfoDto>> filterHabitsByFilters(HabitFilterDto filterDto) {
        List<HabitInfoDto> habitsByFilters = habitService.getHabitsByFilters(filterDto);
        return new SuccessResponse<>(habitsByFilters);
    }

    @GetMapping("/user/{userId}")
    @RequiresAuthorization
    public SuccessResponse<List<HabitInfoDto>> getUserHabits(@PathVariable Long userId) {
        List<HabitInfoDto> allByOwner = habitService.getAllByOwner(userId);
        return new SuccessResponse<>(allByOwner);
    }

    @PutMapping("/{id}/disable")
    @RequiresAuthorization
    public SuccessResponse<String> disableHabit(@PathVariable Long id) {
        habitService.disableHabit(id);
        return new SuccessResponse<>("Привычка отключена");
    }

    @GetMapping("/{id}")
    @RequiresAuthorization
    public SuccessResponse<HabitInfoDto> getHabitById(@PathVariable Long id) {
        HabitInfoDto habitById = habitService.getHabitById(id);
        return new SuccessResponse<>(habitById);
    }

    @DeleteMapping("/{id}")
    @RequiresAuthorization
    public SuccessResponse<String> deleteHabit(@PathVariable Long id) {
        habitService.delete(id);
        return new SuccessResponse<>("Привычка удалена");
    }

    @PostMapping("/{id}/fulfill")
    @RequiresAuthorization
    public void fulfillHabit(SimpleLocalDateDto localDateDto, @PathVariable String id) {
        habitService.markHabitAsFulfilled(Long.valueOf(id), localDateDto);
    }
}
