package io.sabitovka;

import io.sabitovka.controllers.BaseController;
import io.sabitovka.controllers.MainController;
import io.sabitovka.dto.HabitInfoDto;
import io.sabitovka.dto.UserInfoDto;
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

        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setName("admin");
        userInfoDto.setEmail("admin@ylab.ru");
        userInfoDto.setPassword("admin123");
        userService.createUser(userInfoDto);

        habitService.createHabit(new HabitInfoDto("Анжумания", "", Period.ofDays(1), 1L));
        habitService.createHabit(new HabitInfoDto("Прес качат", "", Period.ofDays(1), 1L));
        Habit habit = habitService.createHabit(new HabitInfoDto("Бегит", "", Period.ofDays(1), 1L));
        habitService.disableHabit(habit);

        BaseController mainController = new MainController(authorizationService, userService, habitService);
        mainController.showMenu();
    }
}
