package io.sabitovka.persistence;

import io.sabitovka.config.DataSourceConfig;
import io.sabitovka.persistence.rowmapper.RowMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, который позволяет работать с подключением к БД.
 * Запрашивать данные, выполнять SQL запросы
 */
@AllArgsConstructor
@Component
public class JdbcTemplate {
    private final DataSourceConfig.DataSource dataSource;

    /**
     * Выполняет простой SQL запрос на обновление данных. Возвращает количество затронутых строк.
     * Используется для операций UPDATE
     * @param sql SQL-запрос
     * @param params Параметры запроса
     * @return Количество затронутых строк
     */
    public int executeUpdate(String sql, Object ...params) {
        try (PreparedStatement statement = prepareStatement(sql, params)) {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка выполнения обновления БД: " + e.getMessage());
        }
    }

    /**
     * Выполняет SQL запрос на обновление данных, но возвращает сгенерированные ID.
     * Используется для операций INSERT
     * @param sql SQL-запрос
     * @param params Параметры запроса
     * @return Список идентификаторов новых записей
     */
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

    /**
     * Выполняет операцию выборки данных, помещая их в массив.
     * Может сразу мапить {@link ResultSet} в сущность посредством интерфейса {@link RowMapper} или его реализаций.
     * Используется для операций SELECT
     * @param sql SQL-запрос
     * @param rowMapper Реализация интерфейса {@link RowMapper}
     * @param params Параметры запроса
     * @return Список выбранных объектов из базы данных
     * @param <T> Класс запрашиваемой модели данных
     */
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

    /**
     * Выполняет операцию выборки данных, выбирая первый элемент. Если элементов нет, вернет {@code null}
     * Может сразу мапить {@link ResultSet} в сущность посредством интерфейса {@link RowMapper} или его реализаций.
     * Используется для операций SELECT
     * @param sql SQL-запрос
     * @param rowMapper Реализация интерфейса {@link RowMapper}
     * @param params Параметры запроса
     * @return Полученный объект из БД или {@code null}
     * @param <T> Класс запрашиваемой модели данных
     */
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

    /**
     * Выполняет подготовку запроса для выполнения. Экранирует параметры SQL-запроса
     * @param sql SQL-запрос
     * @param params Параметры запроса
     * @return Подготовленный запроса
     * @throws SQLException Когда не удается подготовить параметры
     */
    private PreparedStatement prepareStatement(String sql, Object ...params) throws SQLException {
        PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                sql.trim().replace("\n", " "),
                Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Enum) {
                preparedStatement.setObject(i + 1, params[i], Types.OTHER);
            } else {
                preparedStatement.setObject(i + 1, params[i]);
            }
        }
        return preparedStatement;
    }
}
