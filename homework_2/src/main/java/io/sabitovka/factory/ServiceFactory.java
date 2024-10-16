package io.sabitovka.factory;

import io.sabitovka.repository.*;
import io.sabitovka.service.AuthorizationService;
import io.sabitovka.service.HabitService;
import io.sabitovka.service.StatisticService;
import io.sabitovka.service.UserService;

public final class ServiceFactory {
    private static final ServiceFactory serviceFactory = new ServiceFactory();

    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final HabitService habitService;

    private final StatisticService statisticService;

    private ServiceFactory() {
        UserRepository userRepository = new UserRepositoryImpl();
        HabitRepository habitRepository = new HabitRepositoryImpl();
        FulfilledHabitRepository fulfilledHabitRepository = new FulfilledHabitRepositoryImpl();

        userService = new UserService(userRepository, habitRepository);
        authorizationService = new AuthorizationService(userRepository);
        habitService = new HabitService(habitRepository, userRepository, fulfilledHabitRepository, userService);
        statisticService = new StatisticService(habitRepository, fulfilledHabitRepository);
    }

    public static synchronized ServiceFactory getInstance() {
        return serviceFactory;
    }

    public UserService getUserService() {
        return userService;
    }

    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public HabitService getHabitService() {
        return habitService;
    }

    public StatisticService getStatisticService() {
        return statisticService;
    }
}
