package io.sabitovka.repository.impl;

import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.model.Habit;
import io.sabitovka.persistence.JdbcTemplate;
import io.sabitovka.persistence.PersistenceRepository;
import io.sabitovka.persistence.rowmapper.FulfilledHabitRowMapper;
import io.sabitovka.persistence.rowmapper.RowMapper;
import io.sabitovka.repository.FulfilledHabitRepository;

import java.util.List;

/**
 * Реализация интерфейса {@link FulfilledHabitRepository} для управления выполненными привычками.
 */
public class FulfilledHabitRepositoryImpl extends PersistenceRepository<Long, FulfilledHabit> implements FulfilledHabitRepository {
    public FulfilledHabitRepositoryImpl(JdbcTemplate jdbcTemplate, RowMapper<FulfilledHabit> rowMapper) {
        super(jdbcTemplate, rowMapper, FulfilledHabit.class);
    }

    @Override
    public boolean update(FulfilledHabit obj) {
        throw new UnsupportedOperationException("Обновление привычки не поддерживается");
    }

    @Override
    public List<FulfilledHabit> findAllByHabit(Habit habit) {
        String sql = "select * from fulfilled_habits where habit_id = ?";
        return jdbcTemplate.queryForList(sql, rowMapper, habit.getId());
    }
}
