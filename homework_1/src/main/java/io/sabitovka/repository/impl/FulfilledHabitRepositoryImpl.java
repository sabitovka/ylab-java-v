package io.sabitovka.repository.impl;

import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.repository.FulfilledHabitRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FulfilledHabitRepositoryImpl implements FulfilledHabitRepository {
    private final AtomicLong fulfilledHabitsCounter = new AtomicLong(0);

    private final Map<Long, FulfilledHabit> fulfilledHabits = new HashMap<>();

    @Override
    public boolean existsById(Long id) {
        return fulfilledHabits.containsKey(id);
    }

    @Override
    public FulfilledHabit create(FulfilledHabit obj) {
        if (obj == null) {
            throw new IllegalArgumentException("FulfilledHabit is null");
        }

        if (existsById(obj.getId())) {
            throw new EntityAlreadyExistsException(obj.getId());
        }

        long objId = fulfilledHabitsCounter.incrementAndGet();

        FulfilledHabit newFulfilledHabit = obj.toBuilder().build();
        newFulfilledHabit.setId(objId);

        fulfilledHabits.put(objId, newFulfilledHabit);

        return newFulfilledHabit.toBuilder().build();
    }

    @Override
    public Optional<FulfilledHabit> findById(Long id) {
        FulfilledHabit fulfilledHabit = fulfilledHabits.get(id);
        if (fulfilledHabit == null) {
            return Optional.empty();
        }
        return Optional.of(fulfilledHabit.toBuilder().build());
    }

    @Override
    public List<FulfilledHabit> findAll() {
        return fulfilledHabits.values().stream()
                .map(fulfilledHabit -> fulfilledHabit.toBuilder().build())
                .toList();
    }

    @Override
    public boolean update(FulfilledHabit obj) {
        throw new UnsupportedOperationException("Обновление привычки не поддерживается");
    }

    @Override
    public boolean deleteById(Long id) {
        return fulfilledHabits.remove(id) != null;
    }
}
