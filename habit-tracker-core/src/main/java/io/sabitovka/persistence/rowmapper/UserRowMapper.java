package io.sabitovka.persistence.rowmapper;

import io.sabitovka.model.User;
import io.sabitovka.util.EntityMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) {
        return EntityMapper.mapResultSetToEntity(resultSet, User.class);
    }
}
