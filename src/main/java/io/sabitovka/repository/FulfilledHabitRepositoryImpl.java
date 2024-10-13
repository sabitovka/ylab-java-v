package io.sabitovka.repository;

import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FulfilledHabitRepositoryImpl implements FulfilledHabitRepository {

    private final AtomicLong fulfilledHabitsCounter = new AtomicLong(0);

    private final Map<Long, FulfilledHabit> fulfilledHabits = new HashMap<>();

    @Override
    public boolean existsById(Long id) {
        return fulfilledHabits.containsKey(id);
    }

    @Override
    public FulfilledHabit create(FulfilledHabit obj) {
        return null;
    }

    @Override
    public Optional<FulfilledHabit> findById(Long id) {
        FulfilledHabit fulfilledHabit = fulfilledHabits.get(id);
        if (fulfilledHabit == null) {
            return Optional.empty();
        }
        return Optional.of(new FulfilledHabit(fulfilledHabit));
    }

    @Override
    public List<FulfilledHabit> findAll() {
        return fulfilledHabits.values().stream().map(FulfilledHabit::new).collect(Collectors.toList());
    }

    @Override
    public boolean update(FulfilledHabit obj) {
        return false;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }
}
