package io.sabitovka.controller;

import io.sabitovka.annotation.RequiresAuthorization;
import io.sabitovka.dto.habit.HabitFilterDto;
import io.sabitovka.dto.habit.HabitInfoDto;
import io.sabitovka.dto.habit.SimpleLocalDateDto;
import io.sabitovka.factory.ServiceFactory;
import io.sabitovka.service.HabitService;
import io.sabitovka.servlet.RestController;
import io.sabitovka.servlet.annotation.*;
import io.sabitovka.servlet.util.SuccessResponse;

import java.util.List;

/**
 * REST-контроллер для управления привычками пользователя
 */
@RequestMapping("/habits")
public class HabitsRestController implements RestController {
    HabitService habitService = ServiceFactory.getInstance().getHabitService();

    @PostMapping("/")
    @RequiresAuthorization
    public SuccessResponse<HabitInfoDto> createHabit(HabitInfoDto habitInfoDto) {
        HabitInfoDto created = habitService.createHabit(habitInfoDto);
        return new SuccessResponse<>(created);
    }

    @PutMapping("/{id|\\d+}")
    @RequiresAuthorization
    public SuccessResponse<String> updateHabit(HabitInfoDto habitInfoDto, String habitId) {
        habitService.updateHabit(Long.valueOf(habitId), habitInfoDto);
        return new SuccessResponse<>("Привычка обновлена");
    }

    @PostMapping("/filter")
    @RequiresAuthorization
    public SuccessResponse<List<HabitInfoDto>> filterHabitsByFilters(HabitFilterDto filterDto) {
        List<HabitInfoDto> habitsByFilters = habitService.getHabitsByFilters(filterDto);
        return new SuccessResponse<>(habitsByFilters);
    }

    @GetMapping("/user/{id|\\d+}")
    @RequiresAuthorization
    public SuccessResponse<List<HabitInfoDto>> getUserHabits(String userId) {
        List<HabitInfoDto> allByOwner = habitService.getAllByOwner(Long.valueOf(userId));
        return new SuccessResponse<>(allByOwner);
    }

    @PutMapping("/{id|\\d+}/disable")
    @RequiresAuthorization
    public SuccessResponse<String> disableHabit(String habitId) {
        habitService.disableHabit(Long.valueOf(habitId));
        return new SuccessResponse<>("Привычка отключена");
    }

    @GetMapping("/{id|\\d+}")
    @RequiresAuthorization
    public SuccessResponse<HabitInfoDto> getHabitById(String habitId) {
        HabitInfoDto habitById = habitService.getHabitById(Long.valueOf(habitId));
        return new SuccessResponse<>(habitById);
    }

    @DeleteMapping("/{id|\\d+}")
    @RequiresAuthorization
    public SuccessResponse<String> deleteHabit(String habitId) {
        habitService.delete(Long.valueOf(habitId));
        return new SuccessResponse<>("Привычка удалена");
    }

    @PostMapping("/{id|\\d+}/fulfill")
    @RequiresAuthorization
    public void fulfillHabit(SimpleLocalDateDto localDateDto, String habitId) {
        habitService.markHabitAsFulfilled(Long.valueOf(habitId), localDateDto);
    }
}
