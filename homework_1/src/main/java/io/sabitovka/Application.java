package io.sabitovka;

import io.sabitovka.controllers.BaseController;
import io.sabitovka.controllers.MainController;
import io.sabitovka.factory.ServiceFactory;
import io.sabitovka.service.impl.AuthorizationServiceImpl;
import io.sabitovka.service.impl.HabitServiceImpl;
import io.sabitovka.service.impl.StatisticServiceImpl;
import io.sabitovka.service.impl.UserServiceImpl;
import io.sabitovka.util.DataMocker;

public class Application {
    public static void main(String[] args) {
        AuthorizationServiceImpl authorizationService = ServiceFactory.getInstance().getAuthorizationService();
        UserServiceImpl userService = ServiceFactory.getInstance().getUserService();
        HabitServiceImpl habitService = ServiceFactory.getInstance().getHabitService();
        StatisticServiceImpl statisticService = ServiceFactory.getInstance().getStatisticService();

        DataMocker.mockData(userService, habitService);

        BaseController mainController = new MainController(authorizationService, userService, statisticService, habitService);
        mainController.showMenu();
    }
}
