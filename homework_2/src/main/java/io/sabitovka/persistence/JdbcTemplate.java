package io.sabitovka.persistence;

import io.sabitovka.persistence.rowmapper.RowMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, который позволяет работать с подключением к БД.
 * Запрашивать данные, выполнять SQL запросы
 */
public class JdbcTemplate implements AutoCloseable {
    private final Connection connection;

    public JdbcTemplate(Connection connection) {
        this.connection = connection;
    }

    public int executeUpdate(String sql, Object ...params) {
        try (PreparedStatement statement = prepareStatement(sql, params)) {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка выполнения обновления БД: " + e.getMessage());
        }
    }

    public List<Long> executeInsert(String sql, Object ...params) {
        try (PreparedStatement statement = prepareStatement(sql, params)) {
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Создание сущности не выполнено, строки не были затронуты.");
            }

            List<Long> generatedKeys = new ArrayList<>();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                while (keys.next()) {
                    generatedKeys.add(keys.getLong(1));
                }
            }

            return generatedKeys;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при вставке данных в БД: " + e.getMessage(), e);
        }
    }

    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object ...params) {
        try (PreparedStatement statement = prepareStatement(sql, params);
             ResultSet resultSet = statement.executeQuery()) {

            List<T> results = new ArrayList<>();
            while (resultSet.next()) {
                results.add(rowMapper.mapRow(resultSet, resultSet.getRow()));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при выполнении SQL-запроса", e);
        }
    }

    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object ...params) {
        try (PreparedStatement statement = prepareStatement(sql, params);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return rowMapper.mapRow(resultSet, resultSet.getRow());
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка выполнения запроса к БД: " + e.getMessage());
        }
    }

    private PreparedStatement prepareStatement(String sql, Object ...params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql.trim().replace("\n", " "), Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Enum) {
                preparedStatement.setObject(i + 1, params[i], Types.OTHER);
            } else {
                preparedStatement.setObject(i + 1, params[i]);
            }
        }
        return preparedStatement;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
