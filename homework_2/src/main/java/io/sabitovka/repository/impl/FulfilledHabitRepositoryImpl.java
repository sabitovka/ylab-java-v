package io.sabitovka.repository.impl;

import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.repository.FulfilledHabitRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализация интерфейса {@link FulfilledHabitRepository} для управления выполненными привычками в памяти с использованием {@link HashMap}.
 */
public class FulfilledHabitRepositoryImpl implements FulfilledHabitRepository {
    /**
     * Счётчик для генерации уникальных идентификаторов выполненных привычек.
     */
    private final AtomicLong fulfilledHabitsCounter = new AtomicLong(0);

    /**
     * Хранилище выполненных привычек, где ключ — это ID привычки, а значение — {@link FulfilledHabit}
     */
    private final Map<Long, FulfilledHabit> fulfilledHabits = new HashMap<>();

    /**
     * Проверяет, существует ли выполненная привычка по её идентификатору.
     *
     * @param id идентификатор выполненной привычки
     * @return true, если привычка существует, иначе false
     */
    @Override
    public boolean existsById(Long id) {
        return fulfilledHabits.containsKey(id);
    }

    /**
     * Создаёт новую выполненную привычку и сохраняет её.
     *
     * @param obj объект {@link FulfilledHabit}, который нужно создать
     * @return созданная выполненная привычка
     * @throws IllegalArgumentException если объект привычки null
     * @throws EntityAlreadyExistsException если привычка с таким ID уже существует
     */
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

    /**
     * Ищет выполненную привычку по её идентификатору.
     *
     * @param id идентификатор выполненной привычки
     * @return {@link Optional}, содержащий найденную привычку, или пустой, если привычка не найдена
     */
    @Override
    public Optional<FulfilledHabit> findById(Long id) {
        FulfilledHabit fulfilledHabit = fulfilledHabits.get(id);
        if (fulfilledHabit == null) {
            return Optional.empty();
        }
        return Optional.of(fulfilledHabit.toBuilder().build());
    }

    /**
     * Возвращает список всех выполненных привычек.
     *
     * @return список выполненных привычек
     */
    @Override
    public List<FulfilledHabit> findAll() {
        return fulfilledHabits.values().stream()
                .map(fulfilledHabit -> fulfilledHabit.toBuilder().build())
                .toList();
    }

    /**
     * Обновление выполненной привычки не поддерживается.
     *
     * @param obj объект {@link FulfilledHabit}, который нужно обновить
     * @return всегда выбрасывает {@link UnsupportedOperationException}
     */
    @Override
    public boolean update(FulfilledHabit obj) {
        throw new UnsupportedOperationException("Обновление привычки не поддерживается");
    }

    /**
     * Удаляет выполненную привычку по её идентификатору.
     *
     * @param id идентификатор выполненной привычки
     * @return true, если привычка была успешно удалена, иначе false
     */
    @Override
    public boolean deleteById(Long id) {
        return fulfilledHabits.remove(id) != null;
    }
}
