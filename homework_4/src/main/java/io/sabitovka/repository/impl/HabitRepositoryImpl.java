package io.sabitovka.repository.impl;

import io.sabitovka.model.Habit;
import io.sabitovka.persistence.JdbcTemplate;
import io.sabitovka.persistence.PersistenceRepository;
import io.sabitovka.persistence.rowmapper.HabitRowMapper;
import io.sabitovka.persistence.rowmapper.RowMapper;
import io.sabitovka.repository.HabitRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Реализация интерфейса {@link HabitRepository} для управления привычками.
 * Хранилище предназначено для работы с объектами типа {@link Habit}, которые привязаны к конкретному пользователю.
 */
@Repository
public class HabitRepositoryImpl extends PersistenceRepository<Long, Habit> implements HabitRepository {
    public HabitRepositoryImpl(JdbcTemplate jdbcTemplate, HabitRowMapper rowMapper) {
        super(jdbcTemplate, rowMapper, Habit.class);
    }

    @Override
    public List<Habit> findAllByUserId(Long ownerId) {
        String sql = "select * from habits where owner_id = ?";
        return jdbcTemplate.queryForList(sql, rowMapper, ownerId);
    }

    @Override
    public List<Habit> filterByUserAndTimeAndStatus(Long ownerId, LocalDate startDate, LocalDate endDate, Boolean isActive) {
        String sql = """
            select * from habits where owner_id = ?
            and (? is null or created_at >= ?)
            and (? is null or created_at <= ?)
            and (? is null or is_active = ?)""";
        return jdbcTemplate.queryForList(
                sql,
                rowMapper,
                ownerId,
                startDate,
                startDate,
                endDate,
                endDate,
                isActive,
                isActive
        );
    }
}

