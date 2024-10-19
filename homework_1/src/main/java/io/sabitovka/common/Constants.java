package io.sabitovka.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String SALT = "2fO0utNp$56";
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_-]{3,16}$";
    public static final String EMAIL_REGEX = ".+@.+\\..+";
    public static final String PASSWORD_REGEX = ".{6,}";
    public static final String HABIT_NAME = ".{3,30}";
}
