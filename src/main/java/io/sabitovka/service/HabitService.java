package io.sabitovka.service;

import io.sabitovka.dto.HabitInfoDto;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.repository.UserRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    public HabitService(HabitRepository habitRepository, UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }

    public Habit createHabit(HabitInfoDto habitInfoDto, User owner) {
        if (habitInfoDto.getName() == null || habitInfoDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название привычки не может быть пустым");
        }

        if (owner == null || !userRepository.existsById(owner.getId())) {
            throw new IllegalArgumentException("Владелец привычки должен быть валидным пользователем");
        }

        Habit newHabit = new Habit(habitInfoDto.getName(), habitInfoDto.getDescription(), habitInfoDto.getFrequency(), owner);
        return habitRepository.create(newHabit);
    }

    public List<HabitInfoDto> getHabitsByFilters(User currentUser, LocalDate startDate, LocalDate endDate, Boolean isActive) {
        List<Habit> habits = habitRepository.filterByUserAndTimeAndStatus(currentUser, startDate, endDate, isActive);
        return habits.stream().map(this::mapHabitToDto).collect(Collectors.toList());
    }

    public List<HabitInfoDto> getAllByOwner(User currentUser) {
        List<Habit> habits = habitRepository.findAllByUser(currentUser);
        return habits.stream().map(this::mapHabitToDto).collect(Collectors.toList());
    }

    public void disableHabit(Habit habit) {
        habit.setActive(false);
        habitRepository.update(habit);
    }

    public HabitInfoDto getHabitById(Long id) {
        Habit habit = habitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Привычка не найдена"));
        return mapHabitToDto(habit);
    }

    public void updateHabit(HabitInfoDto updatedHabit) {
        Habit habit = new Habit(updatedHabit.getId(), updatedHabit.getName(), updatedHabit.getDescription(), updatedHabit.getFrequency(), updatedHabit.getOwner());
        habitRepository.update(habit);
    }

    public void delete(Long habitId) {
        habitRepository.deleteById(habitId);
    }

    private HabitInfoDto mapHabitToDto(Habit habit) {
        return new HabitInfoDto(habit.getId(), habit.getName(), habit.getDescription(), habit.getFrequency(), habit.getCreatedAt(), habit.isActive(), habit.getOwner());
    }
}
