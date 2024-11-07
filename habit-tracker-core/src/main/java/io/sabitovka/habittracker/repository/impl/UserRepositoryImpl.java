package io.sabitovka.habittracker.repository.impl;

import io.sabitovka.habittracker.model.User;
import io.sabitovka.habittracker.persistence.JdbcTemplate;
import io.sabitovka.habittracker.persistence.PersistenceRepository;
import io.sabitovka.habittracker.persistence.rowmapper.UserRowMapper;
import io.sabitovka.habittracker.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Реализация интерфейса {@link UserRepository} для управления пользователями.
 */
@Component
public class UserRepositoryImpl extends PersistenceRepository<Long, User> implements UserRepository {
    public UserRepositoryImpl(JdbcTemplate jdbcTemplate, UserRowMapper rowMapper) {
        super(jdbcTemplate, rowMapper, User.class);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        String sql = "select * from users where email = ?";
        User user = jdbcTemplate.queryForObject(sql, rowMapper, email);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }
}

