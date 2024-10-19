package io.sabitovka.service.impl;

import io.sabitovka.dto.HabitInfoDto;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import io.sabitovka.repository.FulfilledHabitRepository;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.service.HabitService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HabitServiceImpl implements HabitService {
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final FulfilledHabitRepository fulfilledHabitRepository;

    public HabitServiceImpl(HabitRepository habitRepository, UserRepository userRepository, FulfilledHabitRepository fulfilledHabitRepository) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
        this.fulfilledHabitRepository = fulfilledHabitRepository;
    }

    private Habit getHabitById(Long habitId) {
        return habitRepository.findById(habitId)
                .orElseThrow(() -> new EntityNotFoundException(habitId));
    }

    private void validateHabitOwnership(Habit habit, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(ownerId));

        if (!habit.getOwnerId().equals(owner.getId()) && !owner.isAdmin()) {
            throw new IllegalArgumentException("У пользователя нет прав на эту привычку");
        }
    }

    @Override
    public Habit createHabit(HabitInfoDto habitInfoDto) {
        if (habitInfoDto.getName() == null || habitInfoDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название привычки не может быть пустым");
        }

        if (habitInfoDto.getOwnerId() == null || !userRepository.existsById(habitInfoDto.getOwnerId())) {
            throw new IllegalArgumentException("Владелец привычки должен быть валидным пользователем");
        }

        Habit newHabit = new Habit();
        newHabit.setName(habitInfoDto.getName());
        newHabit.setDescription(habitInfoDto.getDescription());
        newHabit.setFrequency(habitInfoDto.getFrequency());
        newHabit.setOwnerId(habitInfoDto.getOwnerId());
        return habitRepository.create(newHabit);
    }

    @Override
    public List<HabitInfoDto> getHabitsByFilters(User currentUser, LocalDate startDate, LocalDate endDate, Boolean isActive) {
        List<Habit> habits = habitRepository.filterByUserAndTimeAndStatus(currentUser, startDate, endDate, isActive);
        return habits.stream()
                .map(this::mapHabitToHabitInfoDto)
                .toList();
    }

    @Override
    public List<HabitInfoDto> getAllByOwner(User currentUser) {
        List<Habit> habits = habitRepository.findAllByUser(currentUser);
        return habits.stream()
                .map(this::mapHabitToHabitInfoDto)
                .toList();
    }

    @Override
    public void disableHabit(Habit habit) {
        habit.setActive(false);
        habitRepository.update(habit);
    }

    @Override
    public HabitInfoDto getHabitById(Long id, Long userId) {
        Habit habit = getHabitById(id);
        validateHabitOwnership(habit, userId);
        return mapHabitToHabitInfoDto(habit);
    }

    @Override
    public void updateHabit(HabitInfoDto updatedHabit, Long currentUserId) {
        Habit habit = getHabitById(updatedHabit.getId());
        validateHabitOwnership(habit, currentUserId);

        habit.setName(updatedHabit.getName());
        habit.setDescription(updatedHabit.getDescription());
        habit.setFrequency(updatedHabit.getFrequency());

        habitRepository.update(habit);
    }

    @Override
    public void delete(Long habitId, Long currentUserId) {
        Habit habit = getHabitById(habitId);
        validateHabitOwnership(habit, currentUserId);

        habitRepository.deleteById(habitId);
        fulfilledHabitRepository.findAll().stream()
                .filter(fulfilledHabit -> fulfilledHabit.getHabitId().equals(habitId))
                .forEach(fulfilledHabit -> fulfilledHabitRepository.deleteById(fulfilledHabit.getId()));
    }

    @Override
    public void markHabitAsFulfilled(Long habitId, LocalDate date, Long currentUserId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Не удалось найти привычку с id=" + habitId));

        validateHabitOwnership(habit, currentUserId);

        FulfilledHabit fulfilledHabit = new FulfilledHabit();
        fulfilledHabit.setHabitId(habitId);
        fulfilledHabit.setFulfillDate(date);

        fulfilledHabitRepository.create(fulfilledHabit);
    }

    @Override
    public HabitInfoDto mapHabitToHabitInfoDto(Habit habit) {
        HabitInfoDto habitInfoDto = new HabitInfoDto();
        habitInfoDto.setId(habit.getId());
        habitInfoDto.setName(habit.getName());
        habitInfoDto.setDescription(habit.getDescription());
        habitInfoDto.setFrequency(habit.getFrequency());
        habitInfoDto.setCreatedAt(habit.getCreatedAt());
        habitInfoDto.setActive(habit.isActive());
        habitInfoDto.setId(habit.getOwnerId());
        return habitInfoDto;
    }
}
