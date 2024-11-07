package io.sabitovka.habittracker.persistence.rowmapper;

import java.sql.ResultSet;

public interface RowMapper<T> {
    T mapRow(ResultSet resultSet, int rowNum);
}
