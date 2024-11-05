package io.sabitovka.repository.impl;

import io.sabitovka.model.Habit;
import io.sabitovka.persistence.JdbcTemplate;
import io.sabitovka.persistence.PersistenceRepository;
import io.sabitovka.persistence.rowmapper.RowMapper;
import io.sabitovka.repository.HabitRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Реализация интерфейса {@link HabitRepository} для управления привычками.
 * Хранилище предназначено для работы с объектами типа {@link Habit}, которые привязаны к конкретному пользователю.
 */
public class HabitRepositoryImpl extends PersistenceRepository<Long, Habit> implements HabitRepository {
    public HabitRepositoryImpl(JdbcTemplate jdbcTemplate, RowMapper<Habit> rowMapper) {
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

    @Override
    public void deleteWithHistoryByHabit(Habit habit) {
        String sqlHistory = "delete from fulfilled_habit where habit_id = ?";
        jdbcTemplate.executeUpdate(sqlHistory, habit.getId());

        this.deleteById(habit.getId());
    }
}

