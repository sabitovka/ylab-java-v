package io.sabitovka.repository.impl;

import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import io.sabitovka.repository.HabitRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса {@link HabitRepository} для управления привычками в памяти.
 * Использует {@link HashMap} для хранения привычек и обеспечивает уникальные идентификаторы с помощью {@link AtomicLong}.
 * Хранилище предназначено для работы с объектами типа {@link Habit}, которые привязаны к конкретному пользователю.
 */
public class HabitRepositoryImpl implements HabitRepository {
    /**
     * Счетчик для генерации уникальных идентификаторов привычек.
     */
    private final AtomicLong habitCounter = new AtomicLong(0);

    /**
     * Хранилище привычек, где ключом является их уникальный идентификатор.
     */
    private final Map<Long, Habit> habits = new HashMap<>();

    /**
     * Проверяет, существует ли привычка с указанным идентификатором.
     *
     * @param id идентификатор привычки.
     * @return {@code true}, если привычка существует, иначе {@code false}.
     */
    @Override
    public boolean existsById(Long id) {
        return habits.containsKey(id);
    }

    /**
     * Создает новую привычку в системе.
     *
     * @param obj объект типа {@link Habit}, представляющий создаваемую привычку.
     * @return созданная привычка с присвоенным идентификатором.
     * @throws IllegalArgumentException если привычка равна null.
     * @throws EntityAlreadyExistsException если привычка с таким ID уже существует.
     */
    @Override
    public Habit create(Habit obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Habit is null");
        }

        if (existsById(obj.getId())) {
            throw new EntityAlreadyExistsException(obj.getId());
        }

        long habitId = habitCounter.incrementAndGet();

        Habit newHabit = obj.toBuilder().build();
        newHabit.setId(habitId);

        habits.put(habitId, newHabit);

        return newHabit.toBuilder().build();
    }

    /**
     * Находит привычку по ее идентификатору.
     *
     * @param id идентификатор привычки.
     * @return {@link Optional}, содержащий привычку, если она найдена, или пустой {@link Optional}.
     */
    @Override
    public Optional<Habit> findById(Long id) {
        Habit habit = habits.get(id);
        if (habit == null) {
            return Optional.empty();
        }
        return Optional.of(habit.toBuilder().build());
    }

    /**
     * Возвращает список всех привычек в системе.
     *
     * @return список всех привычек.
     */
    @Override
    public List<Habit> findAll() {
        return habits.values().stream()
                .map(habit -> habit.toBuilder().build())
                .toList();
    }

    /**
     * Обновляет данные существующей привычки.
     *
     * @param obj обновленные данные привычки.
     * @return {@code true}, если обновление выполнено успешно.
     * @throws IllegalArgumentException если привычка или ее идентификатор равны null.
     * @throws EntityNotFoundException если привычка не найдена.
     */
    @Override
    public boolean update(Habit obj) {
        if (obj == null || obj.getId() == null) {
            throw new IllegalArgumentException("Habit is null or habit.id is null");
        }

        Habit existedHabit = habits.get(obj.getId());
        if (existedHabit == null) {
            throw new EntityNotFoundException(obj.getId());
        }

        existedHabit.setName(obj.getName());
        existedHabit.setDescription(obj.getDescription());
        existedHabit.setActive(obj.isActive());
        existedHabit.setFrequency(obj.getFrequency());

        return true;
    }

    /**
     * Удаляет привычку по ее идентификатору.
     *
     * @param id идентификатор привычки.
     * @return {@code true}, если привычка была успешно удалена.
     */
    @Override
    public boolean deleteById(Long id) {
        return habits.remove(id) != null;
    }

    /**
     * Возвращает список всех привычек, принадлежащих указанному пользователю.
     *
     * @param owner пользователь, владеющий привычками.
     * @return список привычек, принадлежащих пользователю.
     */
    @Override
    public List<Habit> findAllByUser(User owner) {
        return habits.values().stream()
                .filter(habit -> Objects.equals(habit.getOwnerId(), owner.getId()))
                .map(habit -> habit.toBuilder().build())
                .toList();
    }

    /**
     * Фильтрует привычки по пользователю, дате создания и статусу активности.
     *
     * @param owner пользователь, владеющий привычками.
     * @param startDate начальная дата для фильтрации.
     * @param endDate конечная дата для фильтрации.
     * @param isActive статус активности привычки.
     * @return список отфильтрованных привычек.
     */
    @Override
    public List<Habit> filterByUserAndTimeAndStatus(User owner, LocalDate startDate, LocalDate endDate, Boolean isActive) {
        return habits.values().stream()
                .filter(habit -> habit.getOwnerId().equals(owner.getId()))
                .filter(habit -> startDate == null || !habit.getCreatedAt().isBefore(startDate))
                .filter(habit -> endDate == null || !habit.getCreatedAt().isAfter(endDate))
                .filter(habit -> isActive == null || habit.isActive() == isActive)
                .map(habit -> habit.toBuilder().build())
                .collect(Collectors.toList());
    }
}

