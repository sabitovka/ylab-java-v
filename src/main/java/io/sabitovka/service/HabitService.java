package io.sabitovka.service;

import io.sabitovka.dto.HabitInfoDto;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import io.sabitovka.repository.FulfilledHabitRepository;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final FulfilledHabitRepository fulfilledHabitRepository;
    private final UserService userService;

    public HabitService(HabitRepository habitRepository, UserRepository userRepository, FulfilledHabitRepository fulfilledHabitRepository, UserService userService) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
        this.fulfilledHabitRepository = fulfilledHabitRepository;
        this.userService = userService;
    }

    private Habit getHabitById(Long habitId) {
        return habitRepository.findById(habitId).orElseThrow(() -> new EntityNotFoundException("Привычка не найдена"));
    }

    private void validateHabitOwnership(Habit habit, Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new EntityNotFoundException("Указанный пользователь не найден"));
        if (!habit.getOwnerId().equals(owner.getId()) && !owner.isAdmin()) {
            throw new IllegalArgumentException("У пользователя нет прав на эту привычку");
        }
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

    public HabitInfoDto getHabitById(Long id, Long userId) {
        Habit habit = getHabitById(id);
        validateHabitOwnership(habit, userId);
        return mapHabitToHabitInfoDto(habit);
    }

    public void updateHabit(HabitInfoDto updatedHabit, Long currentUserId) {
        Habit habit = getHabitById(updatedHabit.getId());
        validateHabitOwnership(habit, currentUserId);

        habit.setName(updatedHabit.getName());
        habit.setDescription(updatedHabit.getDescription());
        habit.setFrequency(updatedHabit.getFrequency());

        habitRepository.update(habit);
    }

    public void delete(Long habitId, Long currentUserId) {
        Habit habit = getHabitById(habitId);
        validateHabitOwnership(habit, currentUserId);

        habitRepository.deleteById(habitId);
    }

    public void markHabitAsFulfilled(Long habitId, LocalDate date, Long currentUserId) {
        Habit habit = habitRepository.findById(habitId).orElseThrow(() -> new IllegalArgumentException("Не удалось найти привычку с id=" + habitId));
        validateHabitOwnership(habit, currentUserId);

        FulfilledHabit fulfilledHabit = new FulfilledHabit();
        fulfilledHabit.setHabitId(habitId);
        fulfilledHabit.setFulfillDate(date);

        fulfilledHabitRepository.create(fulfilledHabit);
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
