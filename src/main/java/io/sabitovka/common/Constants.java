package io.sabitovka.common;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс с константами проекта
 */
@UtilityClass
public class Constants {
    private static final Properties properties = new Properties();

    static {
        try (InputStream in = Constants.class.getResourceAsStream("/application.properties")) {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Используется для шифрования пароля пользователя
     */
    public static final String SALT = properties.getProperty("salt");

    public static final String JWT_SECRET = properties.getProperty("jwt.secret");

    public static final int JWT_EXPIRATION_MINUTES = Integer.parseInt(properties.getProperty("jwt.expirationMinutes", "5"));

    /**
     * Регулярное выражение для проверки email
     */
    public static final String EMAIL_REGEX = ".+@.+\\..+";

    /**
     * Регулярное выражение для проверки пароля
     */
    public static final String PASSWORD_REGEX = ".{6,}";

    /**
     * Расположение файла для миграции БД
     */
    public static final String CHANGELOG_FILE = properties.getProperty("db.changelogFile");

    /**
     * Схема БД для расположения таблиц сущностей
     */
    public static final String MODEL_SCHEMA = properties.getProperty("db.modelSchema");

    /**
     * Схема БД для расположения служебных таблиц
     */
    public static final String SERVICE_SCHEMA = properties.getProperty("db.serviceSchema");

    /**
     * Хост на котором расположена БД
     */
    public static final String DB_HOST = properties.getProperty("db.host");

    /**
     * Порт базы данных
     */
    public static final String DB_PORT = properties.getProperty("db.port");

    /**
     * Имя базы данных
     */
    public static final String DB_NAME = properties.getProperty("db.name");

    /**
     * Имя пользователя БД
     */
    public static final String DB_USERNAME = properties.getProperty("db.username");

    /**
     * Пароль пользователя
     */
    public static final String DB_PASSWORD = properties.getProperty("db.password");

    /**
     * Имя драйвера
     */
    public static final String DRIVER_CLASS_NAME = properties.getProperty("db.driverClassName");
}
