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
import io.sabitovka.service.StatisticService;
import io.sabitovka.service.UserService;

import java.time.LocalDate;
import java.time.Period;

public class Application {
    public static void main(String[] args) {
        AuthorizationService authorizationService = ServiceFactory.getInstance().getAuthorizationService();
        UserService userService = ServiceFactory.getInstance().getUserService();
        HabitService habitService = ServiceFactory.getInstance().getHabitService();
        StatisticService statisticService = ServiceFactory.getInstance().getStatisticService();

        initData(userService, habitService);

        BaseController mainController = new MainController(authorizationService, userService, statisticService, habitService);
        mainController.showMenu();
    }

    private static void initData(UserService userService, HabitService habitService) {
        UserInfoDto adminUserInfo = new UserInfoDto();
        adminUserInfo.setName("admin");
        adminUserInfo.setEmail("admin@ylab.ru");
        adminUserInfo.setPassword("admin123");
        User adminUser = userService.createUser(adminUserInfo);

        Habit adminHabit1 = habitService.createHabit(new HabitInfoDto("Анжумания", "Прокачиваем Анжумания", Period.ofDays(1), adminUser.getId()));
        Habit adminHabit2 = habitService.createHabit(new HabitInfoDto("Пресс качат", "Привычка для ежедневной тренировки пресса", Period.ofDays(1), adminUser.getId()));

        Habit disabledHabit = habitService.createHabit(new HabitInfoDto("Бегать по утрам", "Ежедневные утренние пробежки", Period.ofDays(1), adminUser.getId()));
        habitService.disableHabit(disabledHabit);

        habitService.markHabitAsFulfilled(adminHabit1.getId(), LocalDate.now().minusDays(2));
        habitService.markHabitAsFulfilled(adminHabit1.getId(), LocalDate.now().minusDays(1));
        habitService.markHabitAsFulfilled(adminHabit2.getId(), LocalDate.now().minusDays(1));

        UserInfoDto ivanUserInfo = new UserInfoDto();
        ivanUserInfo.setName("Иван");
        ivanUserInfo.setEmail("ivan@example.com");
        ivanUserInfo.setPassword("ivanpassword");
        User ivanUser = userService.createUser(ivanUserInfo);

        Habit ivanHabit1 = habitService.createHabit(new HabitInfoDto("Читаем буки", "Привычка читать книги каждый день", Period.ofDays(1), ivanUser.getId()));
        Habit ivanHabit2 = habitService.createHabit(new HabitInfoDto("Учить английский", "Изучать английский язык 3 раза в неделю", Period.ofWeeks(1).withDays(3), ivanUser.getId()));

        // Отмечаем выполнение привычек для "Ивана"
        habitService.markHabitAsFulfilled(ivanHabit1.getId(), LocalDate.now().minusDays(3));
        habitService.markHabitAsFulfilled(ivanHabit1.getId(), LocalDate.now().minusDays(2));
        habitService.markHabitAsFulfilled(ivanHabit2.getId(), LocalDate.now().minusWeeks(1));
        habitService.markHabitAsFulfilled(ivanHabit2.getId(), LocalDate.now());

        // Создание пользователя "Мария"
        UserInfoDto mariaUserInfo = new UserInfoDto();
        mariaUserInfo.setName("Мария");
        mariaUserInfo.setEmail("maria@example.com");
        mariaUserInfo.setPassword("mariapassword");
        User mariaUser = userService.createUser(mariaUserInfo);

        // Создание привычек для "Марии"
        Habit mariaHabit1 = habitService.createHabit(new HabitInfoDto("Медитация", "Медитировать каждое утро", Period.ofDays(1), mariaUser.getId()));
        Habit mariaHabit2 = habitService.createHabit(new HabitInfoDto("Йога", "Заниматься йогой по понедельникам и средам", Period.ofWeeks(1).withDays(2), mariaUser.getId()));
        Habit mariaHabit3 = habitService.createHabit(new HabitInfoDto("Бегать по вечерам", "Ежедневные пробежки вечером", Period.ofDays(1), mariaUser.getId()));

        // Отмечаем выполнение привычек для "Марии"
        habitService.markHabitAsFulfilled(mariaHabit1.getId(), LocalDate.now().minusDays(4));
        habitService.markHabitAsFulfilled(mariaHabit1.getId(), LocalDate.now().minusDays(3));
        habitService.markHabitAsFulfilled(mariaHabit3.getId(), LocalDate.now().minusDays(1));
        habitService.markHabitAsFulfilled(mariaHabit3.getId(), LocalDate.now());
    }
}
