package io.sabitovka;

import io.sabitovka.controllers.BaseController;
import io.sabitovka.controllers.MainController;
import io.sabitovka.factory.ServiceFactory;
import io.sabitovka.service.AuthorizationService;
import io.sabitovka.service.HabitService;
import io.sabitovka.service.StatisticService;
import io.sabitovka.service.UserService;
import io.sabitovka.util.DataMocker;
import io.sabitovka.util.MigrationManager;

public class Application {
    public static void main(String[] args) {
        AuthorizationService authorizationService = ServiceFactory.getInstance().getAuthorizationService();
        UserService userService = ServiceFactory.getInstance().getUserService();
        HabitService habitService = ServiceFactory.getInstance().getHabitService();
        StatisticService statisticService = ServiceFactory.getInstance().getStatisticService();

        // DataMocker.mockData(userService, habitService);
        MigrationManager.migrate();

        BaseController mainController = new MainController(authorizationService, userService, statisticService, habitService);
        mainController.showMenu();
    }
}
