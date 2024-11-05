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
    private final static String url = "jdbc:postgresql://%s:%s/%s?currentSchema=%s"
            .formatted(
                    Constants.DB_HOST,
                    Constants.DB_PORT,
                    Constants.DB_NAME,
                    Constants.MODEL_SCHEMA
            );
    private final static String username = Constants.DB_USERNAME;
    private final static String password = Constants.DB_PASSWORD;
    private final static String driverClassName = Constants.DRIVER_CLASS_NAME;

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
