package io.sabitovka.util;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.experimental.UtilityClass;

import java.sql.Connection;

/**
 * Класс для выполнения миграции базы данных через Liquibase. Описание миграции выполняется в отдельной файле
 */
@UtilityClass
public class MigrationManager {
    public static void migrate(Connection connection, String changelogFile, String modelSchema, String serviceSchema) {
        try {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName(modelSchema);
            database.setLiquibaseSchemaName(serviceSchema);
            Liquibase liquibase = new Liquibase(changelogFile, new ClassLoaderResourceAccessor(), database);
            liquibase.update();
        } catch (LiquibaseException e) {
            System.err.println("Произошла ошибка миграции базы данных: " + e.getMessage());
        }
    }
}
