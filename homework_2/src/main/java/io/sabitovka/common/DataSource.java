package io.sabitovka.common;

import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Утилитарный класс, который предоставляет подключение к базе данных
 */
@UtilityClass
public class DataSource {
    // TODO: 20.10.2024 Перевести на application.properties
    private static String url = "jdbc:postgresql://localhost:5432/habit-tracker";
    private static String username = "postgres";
    private static String password = "password";
    private static String driverClassName = "org.postgresql.Driver";

    static {
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Ошибка загрузки драйвера JDBC", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

}
