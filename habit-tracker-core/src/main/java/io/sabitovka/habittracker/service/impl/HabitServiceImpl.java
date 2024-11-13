package io.sabitovka.habittracker.service.impl;

import io.sabitovka.audit.annotation.Audit;
import io.sabitovka.habittracker.auth.AuthInMemoryContext;
import io.sabitovka.habittracker.auth.entity.UserDetails;
import io.sabitovka.habittracker.dto.habit.HabitFilterDto;
import io.sabitovka.habittracker.dto.habit.HabitInfoDto;
import io.sabitovka.habittracker.dto.habit.SimpleLocalDateDto;
import io.sabitovka.habittracker.enums.ErrorCode;
import io.sabitovka.habittracker.exception.ApplicationException;
import io.sabitovka.habittracker.model.FulfilledHabit;
import io.sabitovka.habittracker.model.Habit;
import io.sabitovka.habittracker.model.User;
import io.sabitovka.habittracker.repository.FulfilledHabitRepository;
import io.sabitovka.habittracker.repository.HabitRepository;
import io.sabitovka.habittracker.repository.UserRepository;
import io.sabitovka.habittracker.service.HabitService;
import io.sabitovka.habittracker.util.mapper.HabitMapper;
import io.sabitovka.habittracker.util.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления привычками пользователя. Реализует интерфейс {@link HabitService}
 */
@RequiredArgsConstructor
@Service
public class HabitServiceImpl implements HabitService {
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final FulfilledHabitRepository fulfilledHabitRepository;
    private final HabitMapper habitMapper;

    @Audit(action = "Создана новая привычка")
    @Override
    public HabitInfoDto createHabit(HabitInfoDto habitInfoDto) {
        Validator.validate(habitInfoDto);
        throwIfNotCurrentUserOrNotAdmin(habitInfoDto.getOwnerId());

        if (!userRepository.existsById(habitInfoDto.getOwnerId())) {
            throw new ApplicationException(ErrorCode.USER_NOT_FOUND, "Владелец привычки должен быть валидным пользователем");
        }

        Habit habit = habitMapper.habitInfoDtoToHabit(habitInfoDto);
        Habit saved = habitRepository.create(habit);

        return habitMapper.habitToHabitInfoDto(saved);
    }

    @Audit(action = "Получение привычек по фильтрам")
    @Override
    public List<HabitInfoDto> getHabitsByFilters(HabitFilterDto filterDto) {
        throwIfNotCurrentUserOrNotAdmin(filterDto.getUserId());

        List<Habit> habits = habitRepository.filterByUserAndTimeAndStatus(
                filterDto.getUserId(),
                filterDto.getStartDate(),
                filterDto.getEndDate(),
                filterDto.isActive());

        return habits.stream()
                .map(habitMapper::habitToHabitInfoDto)
                .toList();
    }

    @Audit(action = "Получение привычек пользователя")
    @Override
    public List<HabitInfoDto> getAllByOwner(Long userId) {
        throwIfNotCurrentUserOrNotAdmin(userId);

        List<Habit> habits = habitRepository.findAllByUserId(userId);
        return habits.stream()
                .map(habitMapper::habitToHabitInfoDto)
                .toList();
    }

    @Audit(action = "Отключение привычки")
    @Override
    public void disableHabit(Long habitId) {
        Habit habit = findHabitById(habitId);
        habit.setActive(false);
        habitRepository.update(habit);
    }

    @Audit(action = "Получение привычки по Id")
    @Override
    public HabitInfoDto getHabitById(Long id) {
        Habit habit = findHabitById(id);
        return habitMapper.habitToHabitInfoDto(habit);
    }

    @Audit(action = "Привычка обновлена")
    @Override
    public void updateHabit(Long habitId, HabitInfoDto updatedHabit) {
        Validator.validate(updatedHabit);

        Habit habit = findHabitById(habitId);

        habit.setName(updatedHabit.getName());
        habit.setDescription(updatedHabit.getDescription());
        habit.setFrequency(updatedHabit.getFrequency());

        habitRepository.update(habit);
    }

    @Audit(action = "Удаление привычки")
    @Override
    public void delete(Long habitId) {
        Habit habit = findHabitById(habitId);
        validateHabitOwnership(habit);

        fulfilledHabitRepository.deleteByHabitId(habitId);
        habitRepository.deleteById(habitId);
    }

    @Audit(action = "Выполнение привычки")
    @Override
    public void markHabitAsFulfilled(Long habitId, SimpleLocalDateDto localDateDto) {
        Habit habit = findHabitById(habitId);
        validateHabitOwnership(habit);

        FulfilledHabit fulfilledHabit = new FulfilledHabit();
        fulfilledHabit.setHabitId(habitId);
        fulfilledHabit.setFulfillDate(localDateDto.getDate());

        fulfilledHabitRepository.create(fulfilledHabit);
    }

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
}
