package io.sabitovka.controller;

import io.sabitovka.auth.annotation.RequiresAuthorization;
import io.sabitovka.dto.habit.HabitFilterDto;
import io.sabitovka.dto.habit.HabitInfoDto;
import io.sabitovka.dto.habit.SimpleLocalDateDto;
import io.sabitovka.factory.ServiceFactory;
import io.sabitovka.service.HabitService;
import io.sabitovka.dto.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления привычками пользователя
 */
@RestController
@RequestMapping("/api/habits")
public class HabitsRestController {
    HabitService habitService = ServiceFactory.getInstance().getHabitService();

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

    // NOTE: Не успел сделать маппинг параметров url в функцию. в следующем задании сделаю через GET, честно)
    // TODO: 03.11.2024 Переделать на GET
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
