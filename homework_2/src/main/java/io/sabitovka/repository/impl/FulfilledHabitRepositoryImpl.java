package io.sabitovka.repository.impl;

import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.persistence.JdbcTemplate;
import io.sabitovka.persistence.PersistenceRepository;
import io.sabitovka.persistence.rowmapper.RowMapper;
import io.sabitovka.repository.FulfilledHabitRepository;

import java.util.HashMap;

/**
 * Реализация интерфейса {@link FulfilledHabitRepository} для управления выполненными привычками в памяти с использованием {@link HashMap}.
 */
public class FulfilledHabitRepositoryImpl extends PersistenceRepository<Long, FulfilledHabit> implements FulfilledHabitRepository {
    public FulfilledHabitRepositoryImpl(JdbcTemplate jdbcTemplate, RowMapper<FulfilledHabit> rowMapper) {
        super(jdbcTemplate, rowMapper, FulfilledHabit.class);
    }

    @Override
    public boolean update(FulfilledHabit obj) {
        throw new UnsupportedOperationException("Обновление привычки не поддерживается");
    }
}
