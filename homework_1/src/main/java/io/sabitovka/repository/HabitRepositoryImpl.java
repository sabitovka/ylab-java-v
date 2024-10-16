package io.sabitovka.repository;

import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class HabitRepositoryImpl implements HabitRepository {

    private final AtomicLong habitCounter = new AtomicLong(0);
    private final Map<Long, Habit> habits = new HashMap<>();

    @Override
    public boolean existsById(Long id) {
        return habits.containsKey(id);
    }

    @Override
    public Habit create(Habit obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Habit is null");
        }

        if (existsById(obj.getId())) {
            throw new EntityAlreadyExistsException("Привычка уже существует в системе");
        }

        long habitId = habitCounter.incrementAndGet();

        Habit newHabit = new Habit(obj);
        newHabit.setId(habitId);

        habits.put(habitId, newHabit);

        return new Habit(newHabit);
    }

    @Override
    public Optional<Habit> findById(Long id) {
        Habit habit = habits.get(id);
        if (habit == null) {
            return Optional.empty();
        }
        return Optional.of(new Habit(habit));
    }

    @Override
    public List<Habit> findAll() {
        return habits.values().stream().map(Habit::new).collect(Collectors.toList());
    }

    @Override
    public boolean update(Habit obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Habit is null");
        }

        Habit existedHabit = habits.get(obj.getId());
        if (existedHabit == null) {
            throw new EntityNotFoundException("Привычка не найдена в системе");
        }

        existedHabit.setName(obj.getName());
        existedHabit.setDescription(obj.getDescription());
        existedHabit.setActive(obj.isActive());
        existedHabit.setFrequency(obj.getFrequency());

        return true;
    }

    @Override
    public boolean deleteById(Long id) {
        return habits.remove(id) != null;
    }

    @Override
    public List<Habit> findAllByUser(User owner) {
        return habits.values().stream()
                .filter(habit -> Objects.equals(habit.getOwnerId(), owner.getId()))
                .map(Habit::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<Habit> filterByUserAndTimeAndStatus(User owner, LocalDate startDate, LocalDate endDate, Boolean isActive) {
        return habits.values().stream()
                .filter(habit -> habit.getOwnerId().equals(owner.getId()))
                .filter(habit -> startDate == null || !habit.getCreatedAt().isBefore(startDate))
                .filter(habit -> endDate == null || !habit.getCreatedAt().isAfter(endDate))
                .filter(habit -> isActive == null || habit.isActive() == isActive)
                .map(Habit::new)
                .collect(Collectors.toList());

    }
}
