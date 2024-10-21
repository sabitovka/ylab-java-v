package io.sabitovka.persistence.rowmapper;

import java.sql.ResultSet;

public interface RowMapper<T> {
    T mapRow(ResultSet resultSet, int rowNum);
}
