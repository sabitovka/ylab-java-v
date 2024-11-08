package io.sabitovka.habittracker.util;

import lombok.experimental.UtilityClass;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

@UtilityClass
public class JdbcUtils {
    /**
     * Выполняет заполнение запроса для выполнения. Экранирует параметры SQL-запроса
     * @param params Параметры запроса
     * @return Подготовленный запроса
     * @throws SQLException Когда не удается подготовить параметры
     */
    public static PreparedStatement prepareStatement(PreparedStatement preparedStatement, Object ...params) throws SQLException {
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
