package io.sabitovka.habittracker.factory;

import io.sabitovka.habittracker.repository.FulfilledHabitRepository;
import io.sabitovka.habittracker.repository.HabitRepository;
import io.sabitovka.habittracker.repository.UserRepository;
import io.sabitovka.habittracker.repository.impl.FulfilledHabitRepositoryImpl;
import io.sabitovka.habittracker.repository.impl.HabitRepositoryImpl;
import io.sabitovka.habittracker.repository.impl.UserRepositoryImpl;
import io.sabitovka.habittracker.service.AuthorizationService;
import io.sabitovka.habittracker.service.HabitService;
import io.sabitovka.habittracker.service.StatisticService;
import io.sabitovka.habittracker.service.UserService;
import io.sabitovka.habittracker.service.impl.AuthorizationServiceImpl;
import io.sabitovka.habittracker.service.impl.HabitServiceImpl;
import io.sabitovka.habittracker.service.impl.StatisticServiceImpl;
import io.sabitovka.habittracker.service.impl.UserServiceImpl;
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
