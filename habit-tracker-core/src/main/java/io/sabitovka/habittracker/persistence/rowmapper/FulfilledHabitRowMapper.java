package io.sabitovka.habittracker.persistence.rowmapper;

import io.sabitovka.habittracker.model.FulfilledHabit;
import io.sabitovka.habittracker.util.EntityMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;

@Component
public class FulfilledHabitRowMapper implements RowMapper<FulfilledHabit> {
    @Override
    public FulfilledHabit mapRow(ResultSet resultSet, int rowNum) {
        return EntityMapper.mapResultSetToEntity(resultSet, FulfilledHabit.class);
    }
}
