package io.sabitovka.repository.impl;

import io.sabitovka.model.User;
import io.sabitovka.persistence.JdbcTemplate;
import io.sabitovka.persistence.PersistenceRepository;
import io.sabitovka.persistence.rowmapper.RowMapper;
import io.sabitovka.repository.UserRepository;

import java.util.HashMap;
import java.util.Optional;

/**
 * Реализация интерфейса {@link UserRepository} для управления пользователями в памяти.
 * Использует {@link HashMap} для хранения пользователей и индексацию по email для уникальности.
 * В рамках данного репозитория отсутствует интеграция с базой данных.
 */
public class UserRepositoryImpl extends PersistenceRepository<Long, User> implements UserRepository {
    public UserRepositoryImpl(JdbcTemplate jdbcTemplate, RowMapper<User> rowMapper) {
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

