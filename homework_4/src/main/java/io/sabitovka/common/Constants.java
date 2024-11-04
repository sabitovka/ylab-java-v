package io.sabitovka.common;

import lombok.experimental.UtilityClass;

/**
 * Класс с константами проекта
 */
@UtilityClass
public class Constants {
    /**
     * Регулярное выражение для проверки email
     */
    public static final String EMAIL_REGEX = ".+@.+\\..+";

    /**
     * Регулярное выражение для проверки пароля
     */
    public static final String PASSWORD_REGEX = ".{6,}";
}
