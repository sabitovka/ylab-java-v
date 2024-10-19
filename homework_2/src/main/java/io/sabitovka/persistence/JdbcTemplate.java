package io.sabitovka.persistence;

import io.sabitovka.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcTemplate {

    private final Connection connection;

    public JdbcTemplate(Connection connection) {
        this.connection = connection;
    }

    public int executeUpdate(String sql, Object ...params) {
        try (PreparedStatement statement = prepareStatement(sql, params)) {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка выполнения обновления БД: " + e.getMessage());
        }
    }

    public ResultSet queryForObject(String sql, Object ...params) {
        try (PreparedStatement statement = prepareStatement(sql, params)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка выполнения запроса к БД: " + e.getMessage());
        }
    }

    private PreparedStatement prepareStatement(String sql, Object ...params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        return preparedStatement;
    }
}
