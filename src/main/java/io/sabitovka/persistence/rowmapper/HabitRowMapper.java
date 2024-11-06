package io.sabitovka.persistence.rowmapper;

import io.sabitovka.model.Habit;
import io.sabitovka.util.EntityMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;

@Component
public class HabitRowMapper implements RowMapper<Habit> {
    @Override
    public Habit mapRow(ResultSet resultSet, int rowNum) {
        return EntityMapper.mapResultSetToEntity(resultSet, Habit.class);
    }
}
