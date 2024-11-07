package io.sabitovka.habittracker.persistence.rowmapper;

import io.sabitovka.habittracker.model.Habit;
import io.sabitovka.habittracker.util.EntityMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;

@Component
public class HabitRowMapper implements RowMapper<Habit> {
    @Override
    public Habit mapRow(ResultSet resultSet, int rowNum) {
        return EntityMapper.mapResultSetToEntity(resultSet, Habit.class);
    }
}
