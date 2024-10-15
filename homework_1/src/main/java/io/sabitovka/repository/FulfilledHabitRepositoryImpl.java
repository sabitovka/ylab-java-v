package io.sabitovka.repository;

import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.model.FulfilledHabit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        if (obj == null) {
            throw new IllegalArgumentException("FulfilledHabit is null");
        }

        if (existsById(obj.getId())) {
            throw new EntityAlreadyExistsException("Привычка уже существует в системе");
        }

        long objId = fulfilledHabitsCounter.incrementAndGet();

        FulfilledHabit newFulfilledHabit = new FulfilledHabit(obj);
        newFulfilledHabit.setId(objId);

        fulfilledHabits.put(objId, newFulfilledHabit);

        return new FulfilledHabit(newFulfilledHabit);
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
        throw new UnsupportedOperationException("Обновление привычки не поддерживается");
    }

    @Override
    public boolean deleteById(Long id) {
        return fulfilledHabits.remove(id) != null;
    }
}
