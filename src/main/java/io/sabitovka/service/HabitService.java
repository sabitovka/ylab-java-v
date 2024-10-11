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

    public List<Habit> getHabitsByFilters(User currentUser, LocalDate startDate, LocalDate endDate, Boolean isActive) {
        return habitRepository.filterByUserAndTimeAndStatus(currentUser, startDate, endDate, isActive);
    }

    public List<Habit> getAllByOwner(User currentUser) {
        return habitRepository.findAllByUser(currentUser);
    }

    public void disableHabit(Habit habit) {
        habit.setActive(false);
        habitRepository.update(habit);
    }

    public Habit getHabitById(Long id) {
        return habitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Привычка не найдена"));
    }
}
