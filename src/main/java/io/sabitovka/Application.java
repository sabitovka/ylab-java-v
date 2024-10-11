package io.sabitovka;

import io.sabitovka.controllers.BaseController;
import io.sabitovka.controllers.MainController;
import io.sabitovka.dto.HabitInfoDto;
import io.sabitovka.factory.ServiceFactory;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import io.sabitovka.service.AuthorizationService;
import io.sabitovka.service.HabitService;
import io.sabitovka.service.UserService;

import java.time.Period;

public class Application {
    public static void main(String[] args) {
        AuthorizationService authorizationService = ServiceFactory.getInstance().getAuthorizationService();
        UserService userService = ServiceFactory.getInstance().getUserService();
        HabitService habitService = ServiceFactory.getInstance().getHabitService();

        User user = authorizationService.register("admin", "admin@ylab.ru", "admin123");
        habitService.createHabit(new HabitInfoDto("Анжумания", "", Period.ofDays(1), UserService.userToUserInfoDto(user)));
        habitService.createHabit(new HabitInfoDto("Прес качат", "", Period.ofDays(1), UserService.userToUserInfoDto(user)));
        Habit habit = habitService.createHabit(new HabitInfoDto("Бегит", "", Period.ofDays(1), UserService.userToUserInfoDto(user)));
        habit.setActive(false);
        habitService.disableHabit(habit);

        BaseController mainController = new MainController(authorizationService, userService, habitService);
        mainController.showMenu();
    }
}
