package io.sabitovka.habittracker.repository.impl;

import io.sabitovka.habittracker.model.Habit;
import io.sabitovka.habittracker.persistence.PersistenceRepository;
import io.sabitovka.habittracker.persistence.rowmapper.HabitRowMapper;
import io.sabitovka.habittracker.repository.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Реализация интерфейса {@link HabitRepository} для управления привычками.
 * Хранилище предназначено для работы с объектами типа {@link Habit}, которые привязаны к конкретному пользователю.
 */
@Repository
public class HabitRepositoryImpl extends PersistenceRepository<Long, Habit> implements HabitRepository {
    @Autowired
    public HabitRepositoryImpl(JdbcTemplate jdbcTemplate, HabitRowMapper rowMapper) {
        super(jdbcTemplate, rowMapper, Habit.class);
    }

    @Override
    public List<Habit> findAllByUserId(Long ownerId) {
        String sql = "select * from habits where owner_id = ?";
        return jdbcTemplate.query(sql, rowMapper, ownerId);
    }

    @Override
    public List<Habit> filterByUserAndTimeAndStatus(Long ownerId, LocalDate startDate, LocalDate endDate, Boolean isActive) {
        String sql = """
            select * from habits where owner_id = ?
            and (? is null or created_at >= ?)
            and (? is null or created_at <= ?)
            and (? is null or is_active = ?)""";
        return jdbcTemplate.query(
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
        String sqlHistory = "delete from fulfilled_habits where habit_id = ?";
        jdbcTemplate.update(sqlHistory, habit.getId());

        this.deleteById(habit.getId());
    }
}

