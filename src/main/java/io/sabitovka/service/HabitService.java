package io.sabitovka.service;

import io.sabitovka.dto.HabitInfoDto;
import io.sabitovka.dto.UserInfoDto;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public HabitService(HabitRepository habitRepository, UserRepository userRepository, UserService userService) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public Habit createHabit(HabitInfoDto habitInfoDto) {
        if (habitInfoDto.getName() == null || habitInfoDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название привычки не может быть пустым");
        }

        if (habitInfoDto.getOwnerId() == null || !userRepository.existsById(habitInfoDto.getOwnerId())) {
            throw new IllegalArgumentException("Владелец привычки должен быть валидным пользователем");
        }

        Habit newHabit = new Habit(habitInfoDto.getName(), habitInfoDto.getDescription(), habitInfoDto.getFrequency(), habitInfoDto.getOwnerId());
        return habitRepository.create(newHabit);
    }

    public List<HabitInfoDto> getHabitsByFilters(User currentUser, LocalDate startDate, LocalDate endDate, Boolean isActive) {
        List<Habit> habits = habitRepository.filterByUserAndTimeAndStatus(currentUser, startDate, endDate, isActive);
        return habits.stream().map(this::mapHabitToHabitInfoDto).collect(Collectors.toList());
    }

    public List<HabitInfoDto> getAllByOwner(User currentUser) {
        List<Habit> habits = habitRepository.findAllByUser(currentUser);
        return habits.stream().map(this::mapHabitToHabitInfoDto).collect(Collectors.toList());
    }

    public void disableHabit(Habit habit) {
        habit.setActive(false);
        habitRepository.update(habit);
    }

    public HabitInfoDto getHabitById(Long id) {
        Habit habit = habitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Привычка не найдена"));
        return mapHabitToHabitInfoDto(habit);
    }

    public void updateHabit(HabitInfoDto updatedHabit) {
        User owner = userRepository.findById(updatedHabit.getOwnerId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь привычки не найден"));

        Habit habit = new Habit(
                updatedHabit.getId(),
                updatedHabit.getName(),
                updatedHabit.getDescription(),
                updatedHabit.getFrequency(),
                owner.getId()
        );
        habitRepository.update(habit);
    }

    public void delete(Long habitId) {
        habitRepository.deleteById(habitId);
    }

    public HabitInfoDto mapHabitToHabitInfoDto(Habit habit) {
        return new HabitInfoDto(
                habit.getId(),
                habit.getName(),
                habit.getDescription(),
                habit.getFrequency(),
                habit.getCreatedAt(),
                habit.isActive(),
                habit.getOwnerId()
        );
    }
}
