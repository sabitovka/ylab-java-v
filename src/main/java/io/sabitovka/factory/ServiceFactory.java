package io.sabitovka.factory;

import io.sabitovka.common.DataSource;
import io.sabitovka.persistence.JdbcTemplate;
import io.sabitovka.persistence.rowmapper.FulfilledHabitRowMapper;
import io.sabitovka.persistence.rowmapper.HabitRowMapper;
import io.sabitovka.persistence.rowmapper.UserRowMapper;
import io.sabitovka.repository.AuditRepository;
import io.sabitovka.repository.FulfilledHabitRepository;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.repository.impl.AuditRepositoryImpl;
import io.sabitovka.repository.impl.FulfilledHabitRepositoryImpl;
import io.sabitovka.repository.impl.HabitRepositoryImpl;
import io.sabitovka.repository.impl.UserRepositoryImpl;
import io.sabitovka.service.*;
import io.sabitovka.service.impl.*;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Класс, который устанавливает корректно зависимости сервисного и слоя данных.
 * Реализует паттерн Singleton
 */
public final class ServiceFactory {
    private static final Connection connection;
    private static ServiceFactory serviceFactory;
    @Getter
    private final UserService userService;
    @Getter
    private final AuthorizationService authorizationService;
    @Getter
    private final HabitService habitService;
    @Getter
    private final StatisticService statisticService;
    @Getter
    private final UserDetailsService userDetailsService;
    @Getter
    private final AuditService auditService;

    static {
        try {
            connection = DataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ServiceFactory() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
        UserRepository userRepository = new UserRepositoryImpl(jdbcTemplate, new UserRowMapper());
        HabitRepository habitRepository = new HabitRepositoryImpl(jdbcTemplate, new HabitRowMapper());
        FulfilledHabitRepository fulfilledHabitRepository = new FulfilledHabitRepositoryImpl(jdbcTemplate, new FulfilledHabitRowMapper());
        AuditRepository auditRepository = new AuditRepositoryImpl(jdbcTemplate, null);

        userService = new UserServiceImpl(userRepository, habitRepository);
        authorizationService = new AuthorizationServiceImpl(userRepository);
        habitService = new HabitServiceImpl(habitRepository, userRepository, fulfilledHabitRepository);
        statisticService = new StatisticServiceImpl(habitRepository, fulfilledHabitRepository);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
        auditService = new AuditServiceImpl(auditRepository);
    }

    public static synchronized ServiceFactory getInstance() {
        if (serviceFactory == null) {
            serviceFactory = new ServiceFactory();
        }
        return serviceFactory;
    }
}
