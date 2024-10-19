package io.sabitovka.util;

import io.sabitovka.dto.HabitInfoDto;
import io.sabitovka.dto.UserInfoDto;
import io.sabitovka.enums.HabitFrequency;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import io.sabitovka.service.HabitService;
import io.sabitovka.service.UserService;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class DataMocker {
    public static void mockData(UserService userService, HabitService habitService) {
        UserInfoDto adminUserInfo = new UserInfoDto();
        adminUserInfo.setName("admin");
        adminUserInfo.setEmail("admin@ylab.ru");
        adminUserInfo.setPassword("admin123");
        adminUserInfo.setAdmin(true);
        User adminUser = userService.createUser(adminUserInfo);

        Habit adminHabit1 = habitService.createHabit(new HabitInfoDto(null, "Анжумания", "Прокачиваем Анжумания", HabitFrequency.DAILY, LocalDate.now(), true, adminUser.getId()));
        Habit adminHabit2 = habitService.createHabit(new HabitInfoDto(null, "Пресс качат", "Привычка для ежедневной тренировки пресса", HabitFrequency.DAILY, LocalDate.now(), true, adminUser.getId()));

        Habit disabledHabit = habitService.createHabit(new HabitInfoDto(null, "Бегит", "Ежедневные утренние пробежки", HabitFrequency.DAILY, LocalDate.now(), true, adminUser.getId()));
        habitService.disableHabit(disabledHabit);

        habitService.markHabitAsFulfilled(adminHabit1.getId(), LocalDate.now().minusDays(2), adminUser.getId());
        habitService.markHabitAsFulfilled(adminHabit1.getId(), LocalDate.now().minusDays(1), adminUser.getId());
        habitService.markHabitAsFulfilled(adminHabit2.getId(), LocalDate.now().minusDays(1), adminUser.getId());

        UserInfoDto ivanUserInfo = new UserInfoDto();
        ivanUserInfo.setName("Иван");
        ivanUserInfo.setEmail("ivan@example.com");
        ivanUserInfo.setPassword("ivanpassword");
        User ivanUser = userService.createUser(ivanUserInfo);

        Habit ivanHabit1 = habitService.createHabit(new HabitInfoDto(null, "Читаем буки", "Привычка читать книги каждый день", HabitFrequency.DAILY, LocalDate.now(), true, ivanUser.getId()));
        Habit ivanHabit2 = habitService.createHabit(new HabitInfoDto(null, "Учить английский", "Изучать английский язык раз в неделю", HabitFrequency.WEEKLY, LocalDate.now(), true, ivanUser.getId()));

        // Отмечаем выполнение привычек для "Ивана"
        habitService.markHabitAsFulfilled(ivanHabit1.getId(), LocalDate.now().minusDays(3),  ivanUser.getId());
        habitService.markHabitAsFulfilled(ivanHabit1.getId(), LocalDate.now().minusDays(2),  ivanUser.getId());
        habitService.markHabitAsFulfilled(ivanHabit2.getId(), LocalDate.now().minusWeeks(1),  ivanUser.getId());
        habitService.markHabitAsFulfilled(ivanHabit2.getId(), LocalDate.now(), ivanUser.getId());

        // Создание пользователя "Мария"
        UserInfoDto mariaUserInfo = new UserInfoDto();
        mariaUserInfo.setName("Мария");
        mariaUserInfo.setEmail("maria@example.com");
        mariaUserInfo.setPassword("mariapassword");
        User mariaUser = userService.createUser(mariaUserInfo);

        // Создание привычек для "Марии"
        Habit mariaHabit1 = habitService.createHabit(new HabitInfoDto(null, "Медитация", "Медитировать каждое утро", HabitFrequency.DAILY, LocalDate.now(), true, mariaUser.getId()));
        Habit mariaHabit2 = habitService.createHabit(new HabitInfoDto(null, "Йога", "Заниматься йогой по понедельникам", HabitFrequency.WEEKLY, LocalDate.now(), true, mariaUser.getId()));
        Habit mariaHabit3 = habitService.createHabit(new HabitInfoDto(null, "Бегать по вечерам", "Ежедневные пробежки вечером", HabitFrequency.DAILY, LocalDate.now(), true, mariaUser.getId()));

        // Отмечаем выполнение привычек для "Марии"
        habitService.markHabitAsFulfilled(mariaHabit1.getId(), LocalDate.now().minusDays(4), mariaUser.getId());
        habitService.markHabitAsFulfilled(mariaHabit1.getId(), LocalDate.now().minusDays(3), mariaUser.getId());
        habitService.markHabitAsFulfilled(mariaHabit3.getId(), LocalDate.now().minusDays(1), mariaUser.getId());
        habitService.markHabitAsFulfilled(mariaHabit3.getId(), LocalDate.now(), mariaUser.getId());
    }
}
