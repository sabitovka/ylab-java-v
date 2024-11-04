package io.sabitovka.listener;

import io.sabitovka.config.DataSourceConfig;
import io.sabitovka.util.MigrationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@RequiredArgsConstructor
@Component
public class MigrationInitializer {
    @Value("db.modelSchema")
    private String modelSchema;

    @Value("db.serviceSchema")
    private String serviceSchema;

    @Value("db.changelogFile")
    private String changelogFile;

    private final DataSourceConfig.DataSource dataSource;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() throws SQLException {
        MigrationManager.migrate(dataSource.getConnection(), changelogFile, modelSchema, serviceSchema);
    }
}
