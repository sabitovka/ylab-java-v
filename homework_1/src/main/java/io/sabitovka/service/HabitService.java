package io.sabitovka.service;

import io.sabitovka.dto.HabitInfoDto;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;

import java.time.LocalDate;
import java.util.List;

public interface HabitService {
    Habit createHabit(HabitInfoDto habitInfoDto);
    void updateHabit(HabitInfoDto habitInfoDto, Long currentUserId);
    List<HabitInfoDto> getHabitsByFilters(User currentUser, LocalDate startDate, LocalDate endDate, Boolean isActive);
    List<HabitInfoDto> getAllByOwner(User currentUser);
    void disableHabit(Habit habit);
    HabitInfoDto getHabitById(Long id, Long userId);
    void delete(Long habitId, Long currentUserId);
    void markHabitAsFulfilled(Long habitId, LocalDate date, Long currentUserId);
    HabitInfoDto mapHabitToHabitInfoDto(Habit habit);
}
