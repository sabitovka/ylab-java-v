package io.sabitovka.common;

public final class Constants {
    public static final String SALT = "2fO0utNp$56";
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_-]{3,16}$";
    public static final String EMAIL_REGEX = ".+@.+\\..+";
    public static final String PASSWORD_REGEX = ".{6,}";
    public static final String HABIT_NAME = ".{3,30}";
    public static final String CHANGELOG_FILE = "db/changelog/changelog.xml";
    public static final String MODEL_SCHEMA = "model";
    public static final String SERVICE_SCHEMA = "service";

    private Constants() {}
}
