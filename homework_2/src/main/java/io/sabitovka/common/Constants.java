package io.sabitovka.common;

import lombok.experimental.UtilityClass;

/**
 * Класс с константами проекта
 */
@UtilityClass
public class Constants {
    /**
     * Используется для шифрования пароля пользователя
     */
    public static final String SALT = "2fO0utNp$56";

    /**
     * Регулярное выражение для проверки ввода имени пользователя
     */
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_-]{3,16}$";

    /**
     * Регулярное выражение для проверки email
     */
    public static final String EMAIL_REGEX = ".+@.+\\..+";

    /**
     * Регулярное выражение для проверки пароля
     */
    public static final String PASSWORD_REGEX = ".{6,}";

    /**
     * Регулярное выражение для проверки названия привычки
     */
    public static final String HABIT_NAME = ".{3,30}";

    /**
     * Расположение файла для миграции БД
     */
    public static final String CHANGELOG_FILE = "db/changelog/changelog.xml";

    /**
     * Схема БД для расположения таблиц сущностей
     */
    public static final String MODEL_SCHEMA = "model";

    /**
     * Схема БД для расположения служебных таблиц
     */
    public static final String SERVICE_SCHEMA = "service";
}
