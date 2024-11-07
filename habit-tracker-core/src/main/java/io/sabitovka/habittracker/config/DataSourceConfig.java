package io.sabitovka.habittracker.config;

import io.sabitovka.habittracker.util.YamlPropertySourceFactory;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
public class DataSourceConfig {
    @Value("${db.url}")
    private String url;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Value("${db.driverClassName}")
    private String driverClassName;

    @Bean
    public DataSource getDataSource() {
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load JDBC driver class", e);
        }
        return new DataSource(url, username, password);
    }

    @AllArgsConstructor
    public static class DataSource {
        private String url;
        private String username;
        private String password;

        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(url, username, password);
        }
    }
}
