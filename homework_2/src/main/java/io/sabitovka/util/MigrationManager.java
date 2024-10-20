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
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName(Constants.MODEL_SCHEMA);
            database.setLiquibaseSchemaName(Constants.SERVICE_SCHEMA);
            Liquibase liquibase = new Liquibase(Constants.CHANGELOG_FILE, new ClassLoaderResourceAccessor(), database);
            liquibase.update();
        } catch (SQLException | LiquibaseException e) {
            System.err.println("Произошла ошибка миграции базы данных: " + e.getMessage());
        }
    }

}
