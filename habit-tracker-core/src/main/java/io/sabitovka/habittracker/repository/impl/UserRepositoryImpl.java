package io.sabitovka.habittracker.repository.impl;

import io.sabitovka.habittracker.model.User;
import io.sabitovka.habittracker.persistence.PersistenceRepository;
import io.sabitovka.habittracker.persistence.rowmapper.UserRowMapper;
import io.sabitovka.habittracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Реализация интерфейса {@link UserRepository} для управления пользователями.
 */
@Component
public class UserRepositoryImpl extends PersistenceRepository<Long, User> implements UserRepository {
    @Autowired
    public UserRepositoryImpl(JdbcTemplate jdbcTemplate, UserRowMapper rowMapper) {
        super(jdbcTemplate, rowMapper, User.class);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        String sql = "select * from users where email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, rowMapper, email);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

