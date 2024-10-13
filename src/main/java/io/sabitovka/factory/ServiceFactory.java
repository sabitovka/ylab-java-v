package io.sabitovka.factory;

import io.sabitovka.repository.HabitRepository;
import io.sabitovka.repository.HabitRepositoryImpl;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.repository.UserRepositoryImpl;
import io.sabitovka.service.AuthorizationService;
import io.sabitovka.service.HabitService;
import io.sabitovka.service.UserService;

public final class ServiceFactory {
    private static final ServiceFactory serviceFactory = new ServiceFactory();

    private UserService userService;
    private AuthorizationService authorizationService;
    private HabitService habitService;

    private ServiceFactory() {
        UserRepository userRepository = new UserRepositoryImpl();
        HabitRepository habitRepository = new HabitRepositoryImpl();

        userService = new UserService(userRepository);
        authorizationService = new AuthorizationService(userRepository);
        habitService = new HabitService(habitRepository, userRepository, userService);
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
}
