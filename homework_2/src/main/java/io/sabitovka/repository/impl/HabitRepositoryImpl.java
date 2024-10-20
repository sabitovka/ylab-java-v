package io.sabitovka.repository.impl;

import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import io.sabitovka.persistence.JdbcTemplate;
import io.sabitovka.persistence.PersistenceRepository;
import io.sabitovka.persistence.rowmapper.RowMapper;
import io.sabitovka.repository.HabitRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
        String sql = "select * from habits where owner_id = ?";
        return jdbcTemplate.queryForList(sql, rowMapper, owner.getId());
    }

    @Override
    public List<Habit> filterByUserAndTimeAndStatus(User owner, LocalDate startDate, LocalDate endDate, Boolean isActive) {
        String sql = """
            select * from habits where owner_id = ?
                and (? is null or created_at >= ?)
                and (? is null or created_at <= ?)
                and (? is null or is_active = ?""";
        return jdbcTemplate.queryForList(
                sql,
                rowMapper,
                owner.getId(),
                startDate,
                startDate,
                endDate,
                endDate,
                isActive,
                isActive
        );
    }
}

