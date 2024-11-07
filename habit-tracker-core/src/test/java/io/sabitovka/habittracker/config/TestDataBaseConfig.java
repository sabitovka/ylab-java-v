package io.sabitovka.habittracker.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
public class TestDataBaseConfig {
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:17.0")
            .withDatabaseName("testdb")
            .withUsername("junit")
            .withPassword("password");

    @BeforeAll
    public static void startContainer() {
        postgresContainer.start();
    }

    @AfterAll
    public static void stopContainer() {
        postgresContainer.stop();
    }

    @DynamicPropertySource
    private static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("db.url", () -> postgresContainer.getJdbcUrl() + "&currentSchema=model");
        registry.add("db.username", postgresContainer::getUsername);
        registry.add("db.password", postgresContainer::getPassword);
    }
}
