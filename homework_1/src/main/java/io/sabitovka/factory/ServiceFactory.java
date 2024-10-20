package io.sabitovka.factory;

import io.sabitovka.repository.FulfilledHabitRepository;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.repository.impl.FulfilledHabitRepositoryImpl;
import io.sabitovka.repository.impl.HabitRepositoryImpl;
import io.sabitovka.repository.impl.UserRepositoryImpl;
import io.sabitovka.service.AuthorizationService;
import io.sabitovka.service.HabitService;
import io.sabitovka.service.StatisticService;
import io.sabitovka.service.UserService;
import io.sabitovka.service.impl.AuthorizationServiceImpl;
import io.sabitovka.service.impl.HabitServiceImpl;
import io.sabitovka.service.impl.StatisticServiceImpl;
import io.sabitovka.service.impl.UserServiceImpl;
import lombok.Getter;

/**
 * Класс, который устанавливает корректно зависимости сервисного и слоя данных.
 * Реализует паттерн Singleton
 */
public final class ServiceFactory {
    private static final ServiceFactory serviceFactory = new ServiceFactory();
    @Getter
    private final UserService userService;
    @Getter
    private final AuthorizationService authorizationService;
    @Getter
    private final HabitService habitService;
    @Getter
    private final StatisticService statisticService;

    private ServiceFactory() {
        UserRepository userRepository = new UserRepositoryImpl();
        HabitRepository habitRepository = new HabitRepositoryImpl();
        FulfilledHabitRepository fulfilledHabitRepository = new FulfilledHabitRepositoryImpl();

        userService = new UserServiceImpl(userRepository, habitRepository);
        authorizationService = new AuthorizationServiceImpl(userRepository);
        habitService = new HabitServiceImpl(habitRepository, userRepository, fulfilledHabitRepository);
        statisticService = new StatisticServiceImpl(habitRepository, fulfilledHabitRepository);
    }

    public static synchronized ServiceFactory getInstance() {
        return serviceFactory;
    }
}
