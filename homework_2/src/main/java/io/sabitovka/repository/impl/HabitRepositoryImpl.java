package io.sabitovka.repository.impl;

import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import io.sabitovka.persistence.JdbcTemplate;
import io.sabitovka.persistence.PersistenceRepository;
import io.sabitovka.persistence.rowmapper.RowMapper;
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
public class HabitRepositoryImpl extends PersistenceRepository<Long, Habit> implements HabitRepository {
    public HabitRepositoryImpl(JdbcTemplate jdbcTemplate, RowMapper<Habit> rowMapper) {
        super(jdbcTemplate, rowMapper, Habit.class);
    }

    @Override
    public List<Habit> findAllByUser(User owner) {
        return Collections.emptyList();
//        String sql = "";
//        return habits.values().stream()
//                .filter(habit -> Objects.equals(habit.getOwnerId(), owner.getId()))
//                .map(habit -> habit.toBuilder().build())
//                .toList();
    }

    @Override
    public List<Habit> filterByUserAndTimeAndStatus(User owner, LocalDate startDate, LocalDate endDate, Boolean isActive) {
        return Collections.emptyList();
//        return habits.values().stream()
//                .filter(habit -> habit.getOwnerId().equals(owner.getId()))
//                .filter(habit -> startDate == null || !habit.getCreatedAt().isBefore(startDate))
//                .filter(habit -> endDate == null || !habit.getCreatedAt().isAfter(endDate))
//                .filter(habit -> isActive == null || habit.isActive() == isActive)
//                .map(habit -> habit.toBuilder().build())
//                .collect(Collectors.toList());
    }
}

