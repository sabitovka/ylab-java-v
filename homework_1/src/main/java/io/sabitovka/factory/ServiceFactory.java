package io.sabitovka.factory;

import io.sabitovka.repository.FulfilledHabitRepository;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.repository.impl.FulfilledHabitRepositoryImpl;
import io.sabitovka.repository.impl.HabitRepositoryImpl;
import io.sabitovka.repository.impl.UserRepositoryImpl;
import io.sabitovka.service.impl.AuthorizationServiceImpl;
import io.sabitovka.service.impl.HabitServiceImpl;
import io.sabitovka.service.impl.StatisticServiceImpl;
import io.sabitovka.service.impl.UserServiceImpl;

public final class ServiceFactory {
    private static final ServiceFactory serviceFactory = new ServiceFactory();

    private final UserServiceImpl userService;
    private final AuthorizationServiceImpl authorizationService;
    private final HabitServiceImpl habitService;

    private final StatisticServiceImpl statisticService;

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

    public UserServiceImpl getUserService() {
        return userService;
    }

    public AuthorizationServiceImpl getAuthorizationService() {
        return authorizationService;
    }

    public HabitServiceImpl getHabitService() {
        return habitService;
    }

    public StatisticServiceImpl getStatisticService() {
        return statisticService;
    }
}
