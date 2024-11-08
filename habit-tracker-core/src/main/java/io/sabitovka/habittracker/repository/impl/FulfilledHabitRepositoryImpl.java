package io.sabitovka.habittracker.repository.impl;

import io.sabitovka.habittracker.model.FulfilledHabit;
import io.sabitovka.habittracker.model.Habit;
import io.sabitovka.habittracker.persistence.PersistenceRepository;
import io.sabitovka.habittracker.persistence.rowmapper.FulfilledHabitRowMapper;
import io.sabitovka.habittracker.repository.FulfilledHabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Реализация интерфейса {@link FulfilledHabitRepository} для управления выполненными привычками.
 */
@Repository
public class FulfilledHabitRepositoryImpl extends PersistenceRepository<Long, FulfilledHabit> implements FulfilledHabitRepository {
    @Autowired
    public FulfilledHabitRepositoryImpl(JdbcTemplate jdbcTemplate, FulfilledHabitRowMapper rowMapper) {
        super(jdbcTemplate, rowMapper, FulfilledHabit.class);
    }

    @Override
    public boolean update(FulfilledHabit obj) {
        throw new UnsupportedOperationException("Обновление привычки не поддерживается");
    }

    @Override
    public List<FulfilledHabit> findAllByHabit(Habit habit) {
        String sql = "select * from fulfilled_habits where habit_id = ?";
        return jdbcTemplate.query(sql, rowMapper, habit.getId());
    }

    @Override
    public void deleteByHabitId(Long habitId) {
        String sql = "delete from fulfilled_habits where habit_id = ?";
        jdbcTemplate.update(sql, habitId);
    }
}
