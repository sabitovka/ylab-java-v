package io.sabitovka.controller;

import io.sabitovka.annotation.RequiresAuthorization;
import io.sabitovka.dto.SuccessResponse;
import io.sabitovka.dto.habit.HabitFilterDto;
import io.sabitovka.dto.habit.HabitInfoDto;
import io.sabitovka.dto.habit.SimpleLocalDateDto;
import io.sabitovka.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createHabit(HabitInfoDto habitInfoDto) {
        HabitInfoDto created = habitService.createHabit(habitInfoDto);
        return ResponseEntity.ok(new SuccessResponse<>(created));
    }

    @PutMapping("/{id}")
    @RequiresAuthorization
    public ResponseEntity<?> updateHabit(HabitInfoDto habitInfoDto, @PathVariable("id") Long id) {
        habitService.updateHabit(id, habitInfoDto);
        return ResponseEntity.ok(new SuccessResponse<>("Привычка обновлена"));
    }

    @PostMapping("/filter")
    @RequiresAuthorization
    public ResponseEntity<?> filterHabitsByFilters(HabitFilterDto filterDto) {
        List<HabitInfoDto> habitsByFilters = habitService.getHabitsByFilters(filterDto);
        return ResponseEntity.ok(new SuccessResponse<>(habitsByFilters));
    }

    @GetMapping("/user/{userId}")
    @RequiresAuthorization
    public ResponseEntity<?>  getUserHabits(@PathVariable("userId") Long userId) {
        List<HabitInfoDto> allByOwner = habitService.getAllByOwner(userId);
        return ResponseEntity.ok(new SuccessResponse<>(allByOwner));
    }

    @PutMapping("/{id}/disable")
    @RequiresAuthorization
    public ResponseEntity<?> disableHabit(@PathVariable("id") Long id) {
        habitService.disableHabit(id);
        return ResponseEntity.ok(new SuccessResponse<>("Привычка отключена"));
    }

    @GetMapping("/{id}")
    @RequiresAuthorization
    public ResponseEntity<?> getHabitById(@PathVariable("id") Long id) {
        HabitInfoDto habitById = habitService.getHabitById(id);
        return ResponseEntity.ok(new SuccessResponse<>(habitById));
    }

    @DeleteMapping("/{id}")
    @RequiresAuthorization
    public ResponseEntity<?> deleteHabit(@PathVariable("id") Long id) {
        habitService.delete(id);
        return ResponseEntity.ok(new SuccessResponse<>("Привычка удалена"));
    }

    @PostMapping("/{id}/fulfill")
    @RequiresAuthorization
    public void fulfillHabit(SimpleLocalDateDto localDateDto, @PathVariable("id") String id) {
        habitService.markHabitAsFulfilled(Long.valueOf(id), localDateDto);
    }
}
