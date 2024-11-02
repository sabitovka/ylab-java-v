package io.sabitovka.habittracker;

import io.sabitovka.habittracker.controllers.BaseController;
import io.sabitovka.habittracker.controllers.MainController;
import io.sabitovka.habittracker.factory.ServiceFactory;
import io.sabitovka.habittracker.service.AuthorizationService;
import io.sabitovka.habittracker.service.HabitService;
import io.sabitovka.habittracker.service.StatisticService;
import io.sabitovka.habittracker.service.UserService;
import io.sabitovka.habittracker.util.DataMocker;

public class Application {
    public static void main(String[] args) {
        AuthorizationService authorizationService = ServiceFactory.getInstance().getAuthorizationService();
        UserService userService = ServiceFactory.getInstance().getUserService();
        HabitService habitService = ServiceFactory.getInstance().getHabitService();
        StatisticService statisticService = ServiceFactory.getInstance().getStatisticService();

        DataMocker.mockData(userService, habitService);

        BaseController mainController = new MainController(authorizationService, userService, statisticService, habitService);
        mainController.showMenu();
    }
}
