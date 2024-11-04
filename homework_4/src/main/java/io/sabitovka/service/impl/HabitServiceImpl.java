package io.sabitovka.service.impl;

import io.sabitovka.auth.AuthInMemoryContext;
import io.sabitovka.auth.entity.UserDetails;
import io.sabitovka.dto.habit.HabitFilterDto;
import io.sabitovka.dto.habit.HabitInfoDto;
import io.sabitovka.dto.habit.SimpleLocalDateDto;
import io.sabitovka.enums.ErrorCode;
import io.sabitovka.exception.ApplicationException;
import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import io.sabitovka.repository.FulfilledHabitRepository;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.service.HabitService;
import io.sabitovka.util.mapper.HabitMapper;
import io.sabitovka.util.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HabitServiceImpl implements HabitService {
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final FulfilledHabitRepository fulfilledHabitRepository;

    private void validateHabitOwnership(Habit habit) {
        UserDetails userDetails = AuthInMemoryContext.getContext().getAuthentication();
        User owner = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND, "Владелец привычки не найден"));

        if (!habit.getOwnerId().equals(owner.getId()) && !owner.isAdmin()) {
            throw new ApplicationException(ErrorCode.FORBIDDEN, "У пользователя нет прав на эту привычку");
        }
    }

    private void throwIfNotCurrentUserOrNotAdmin(Long userId) {
        UserDetails userDetails = AuthInMemoryContext.getContext().getAuthentication();
        if (!userDetails.getUserId().equals(userId) && !userDetails.isAdmin()) {
            throw new ApplicationException(ErrorCode.FORBIDDEN, "Нет доступа к привычке");
        }
    }

    private Habit findHabitById(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.HABIT_NOT_FOUND));

        validateHabitOwnership(habit);
        return habit;
    }

    @Override
    public HabitInfoDto createHabit(HabitInfoDto habitInfoDto) {
        Validator.validate(habitInfoDto);
        throwIfNotCurrentUserOrNotAdmin(habitInfoDto.getOwnerId());

        if (!userRepository.existsById(habitInfoDto.getOwnerId())) {
            throw new ApplicationException(ErrorCode.USER_NOT_FOUND, "Владелец привычки должен быть валидным пользователем");
        }

        Habit habit = HabitMapper.INSTANCE.habitInfoDtoToHabit(habitInfoDto);
        Habit saved = habitRepository.create(habit);

        return HabitMapper.INSTANCE.habitToHabitInfoDto(saved);
    }

    @Override
    public List<HabitInfoDto> getHabitsByFilters(HabitFilterDto filterDto) {
        throwIfNotCurrentUserOrNotAdmin(filterDto.getUserId());

        List<Habit> habits = habitRepository.filterByUserAndTimeAndStatus(
                filterDto.getUserId(),
                filterDto.getStartDate(),
                filterDto.getEndDate(),
                filterDto.isActive());

        return habits.stream()
                .map(HabitMapper.INSTANCE::habitToHabitInfoDto)
                .toList();
    }

    @Override
    public List<HabitInfoDto> getAllByOwner(Long userId) {
        throwIfNotCurrentUserOrNotAdmin(userId);

        List<Habit> habits = habitRepository.findAllByUserId(userId);
        return habits.stream()
                .map(HabitMapper.INSTANCE::habitToHabitInfoDto)
                .toList();
    }

    @Override
    public void disableHabit(Long habitId) {
        Habit habit = findHabitById(habitId);
        habit.setActive(false);
        habitRepository.update(habit);
    }

    @Override
    public HabitInfoDto getHabitById(Long id) {
        Habit habit = findHabitById(id);
        return HabitMapper.INSTANCE.habitToHabitInfoDto(habit);
    }

    @Override
    public void updateHabit(Long habitId, HabitInfoDto updatedHabit) {
        Validator.validate(updatedHabit);

        Habit habit = findHabitById(habitId);

        habit.setName(updatedHabit.getName());
        habit.setDescription(updatedHabit.getDescription());
        habit.setFrequency(updatedHabit.getFrequency());

        habitRepository.update(habit);
    }

    @Override
    public void delete(Long habitId) {
        Habit habit = findHabitById(habitId);
        validateHabitOwnership(habit);

        // TODO: 30.10.2024 Перевести на SQL запросы
        fulfilledHabitRepository.findAll().stream()
                .filter(fulfilledHabit -> fulfilledHabit.getHabitId().equals(habitId))
                .forEach(fulfilledHabit -> fulfilledHabitRepository.deleteById(fulfilledHabit.getId()));
        habitRepository.deleteById(habitId);
    }

    @Override
    public void markHabitAsFulfilled(Long habitId, SimpleLocalDateDto localDateDto) {
        Habit habit = findHabitById(habitId);
        validateHabitOwnership(habit);

        FulfilledHabit fulfilledHabit = new FulfilledHabit();
        fulfilledHabit.setHabitId(habitId);
        fulfilledHabit.setFulfillDate(localDateDto.getDate());

        fulfilledHabitRepository.create(fulfilledHabit);
    }
}
