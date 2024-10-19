package io.sabitovka.persistence;

import io.sabitovka.exception.DatabaseException;
import io.sabitovka.persistence.JdbcTemplate;
import io.sabitovka.repository.BaseRepository;
import io.sabitovka.utils.EntityMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public abstract class PersistenceRepository<I, M> implements BaseRepository<I, M> {
    private final JdbcTemplate jdbcTemplate;
    private final Class<M> modelClass;

    protected PersistenceRepository(JdbcTemplate jdbcTemplate, Class<M> modelClass) {
        this.jdbcTemplate = jdbcTemplate;
        this.modelClass = modelClass;
    }

    @Override
    public Optional<M> findById(I id) {
        Table annotation = modelClass.getAnnotation(Table.class);
        String tableName = annotation.name();

        String sql = "select * from ? where id = ?";
        try (ResultSet resultSet = jdbcTemplate.queryForObject(sql, tableName, id)) {
            if (resultSet == null) {
                return Optional.empty();
            }

            return Optional.of(EntityMapper.mapResultSetToEntity(resultSet, modelClass));
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при поиске сущности по ID: " + e.getMessage());
        }
    }
}
