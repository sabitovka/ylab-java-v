package io.sabitovka.persistence.rowmapper;

import io.sabitovka.model.Habit;
import io.sabitovka.util.EntityMapper;

import java.sql.ResultSet;

public class HabitRowMapper implements RowMapper<Habit> {
    @Override
    public Habit mapRow(ResultSet resultSet, int rowNum) {
        return EntityMapper.mapResultSetToEntity(resultSet, Habit.class);
    }
}
