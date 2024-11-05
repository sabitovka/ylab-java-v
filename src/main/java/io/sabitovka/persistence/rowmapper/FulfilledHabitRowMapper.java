package io.sabitovka.persistence.rowmapper;

import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.util.EntityMapper;

import java.sql.ResultSet;

public class FulfilledHabitRowMapper implements RowMapper<FulfilledHabit> {
    @Override
    public FulfilledHabit mapRow(ResultSet resultSet, int rowNum) {
        return EntityMapper.mapResultSetToEntity(resultSet, FulfilledHabit.class);
    }
}
