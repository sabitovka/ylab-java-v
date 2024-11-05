package io.sabitovka.util;

import io.sabitovka.common.Constants;
import io.sabitovka.common.DataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Класс для выполнения миграции базы данных через Liquibase. Описание миграции выполняется в отдельной файле
 */
@UtilityClass
public class MigrationManager {
    /**
     * Основной метод, который запускает миграцию базы данных, если это необходимо
     */
    public static void migrate() {
        try {
            Connection connection = DataSource.getConnection();
            migrate(connection, Constants.CHANGELOG_FILE);
        } catch (SQLException e) {
            System.err.println("Произошла ошибка создания соединения с БД при выполнении миграции: " + e.getMessage());
        }
    }

    public static void migrate(Connection connection, String changelogFile) {
        try {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName(Constants.MODEL_SCHEMA);
            database.setLiquibaseSchemaName(Constants.SERVICE_SCHEMA);
            Liquibase liquibase = new Liquibase(changelogFile, new ClassLoaderResourceAccessor(), database);
            liquibase.update();
        } catch (LiquibaseException e) {
            System.err.println("Произошла ошибка миграции базы данных: " + e.getMessage());
        }
    }

}
