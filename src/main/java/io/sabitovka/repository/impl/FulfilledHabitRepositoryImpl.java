package io.sabitovka.repository.impl;

import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.model.Habit;
import io.sabitovka.persistence.JdbcTemplate;
import io.sabitovka.persistence.PersistenceRepository;
import io.sabitovka.persistence.rowmapper.FulfilledHabitRowMapper;
import io.sabitovka.repository.FulfilledHabitRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Реализация интерфейса {@link FulfilledHabitRepository} для управления выполненными привычками.
 */
@Repository
public class FulfilledHabitRepositoryImpl extends PersistenceRepository<Long, FulfilledHabit> implements FulfilledHabitRepository {
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
        return jdbcTemplate.queryForList(sql, rowMapper, habit.getId());
    }

    @Override
    public void deleteByHabitId(Long habitId) {
        String sql = "delete from fulfilled_habits where habit_id = ?";
        jdbcTemplate.executeUpdate(sql, habitId);
    }
}
