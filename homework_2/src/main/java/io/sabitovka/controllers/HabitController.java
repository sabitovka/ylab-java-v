package io.sabitovka.controllers;

import io.sabitovka.common.Constants;
import io.sabitovka.dto.HabitInfoDto;
import io.sabitovka.enums.HabitFrequency;
import io.sabitovka.service.AuthorizationService;
import io.sabitovka.service.HabitService;
import io.sabitovka.service.StatisticService;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Scanner;

public class HabitController extends BaseController {

    private final HabitService habitService;
    private final AuthorizationService authorizationService;
    private final StatisticService statisticService;

    public HabitController(Scanner scanner, HabitService habitService, AuthorizationService authorizationService, StatisticService statisticService) {
        super(scanner);
        this.habitService = habitService;
        this.authorizationService = authorizationService;
        this.statisticService = statisticService;
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("=== Редактирование привычек ===");
            System.out.println("Выберите действие из меню");
            System.out.println("1. Добавить привычку");
            System.out.println("2. Изменить привычку");
            System.out.println("3. Удалить привычку");
            System.out.println("4. Просмотреть привычки");
            System.out.println("5. Отметить выполнение привычки");
            System.out.println("6. Получить подробный отчет о выполнении привычки");
            System.out.println("7. Назад");

            String choice = prompt(" -> ", "^[1-7]$");
            try {
                switch (choice) {
                    case "1" -> createHabit();
                    case "2" -> changeHabit();
                    case "3" -> deleteHabit();
                    case "4" -> printAllHabits();
                    case "5" -> markHabitAsFulfilled();
                    case "6" -> fulfillingReport();
                    case "7" -> {
                        System.out.println("Выход в главное меню");
                        return;
                    }
                    default -> System.out.println("Неверный выбор, попробуйте снова");
                }
            } catch (Exception e) {
                System.err.println("Произошла ошибка: " + e.getMessage());
            }
        }
    }

    private HabitInfoDto promptHabitInfo() {
        String name = prompt("Введите название привычки: ", Constants.HABIT_NAME);
        String description = prompt("Введите описание привычки: ", ".{0,255}");
        System.out.println("Введите частоту привычки: 1. Ежедневно, 2. Еженедельно");
        String frequencyChoice = prompt(" -> ", "^[1-2]$");
        HabitFrequency frequency = switch (frequencyChoice) {
            case "1" -> HabitFrequency.DAILY;
            case "2" -> HabitFrequency.WEEKLY;
            default -> throw new IllegalStateException("Unexpected value: " + frequencyChoice);
        };

        Long ownerId = authorizationService.getCurrentUser().getId();
        return new HabitInfoDto(name, description, frequency, ownerId);
    }

    private HabitInfoDto promptForHabit() {
        String habitId = prompt("Введите ID привычки: ", "\\d+");
        return habitService.getHabitById(Long.parseLong(habitId), authorizationService.getCurrentUserId());
    }

    private void createHabit() {
        System.out.println("Добавление привычки");
        HabitInfoDto habitInfoDto = promptHabitInfo();

        habitService.createHabit(habitInfoDto);
        System.out.println("Новая привычка добавлена");
    }

    private void printAllHabits() {
        System.out.println("== Список всех привычек ==");
        boolean useFilter = promptForBoolean("Применить фильтрацию? (1 - Да, 2 - Нет): ");
        List<HabitInfoDto> habits;
        if (useFilter) {
            LocalDate startDate = promptForDate("Введите дату начала фильтрации (dd-MM-yyyy) или пропустите: ");
            LocalDate endDate = promptForDate("Введите дату конца фильтрации (dd-MM-yyyy) или пропустите: ");
            String isActive = prompt("Введите требуемый статус? (1 - Только активные, 2 - Только неактивные, 3 - Все): ", "^[1-3]$");

            Boolean activeFilter = switch (isActive) {
                case "1" -> Boolean.TRUE;
                case "2" -> Boolean.FALSE;
                case "3" -> null;
                default -> throw new IllegalStateException("Unexpected value: " + isActive);
            };

            habits = habitService.getHabitsByFilters(authorizationService.getCurrentUser(), startDate, endDate, activeFilter);
        } else {
            habits = habitService.getAllByOwner(authorizationService.getCurrentUser());
        }

        System.out.println("Найденные привычки:");
        habits.forEach(System.out::println);
    }

    private void changeHabit() {
        System.out.println("Изменить привычку");
        HabitInfoDto habit = promptForHabit();

        System.out.println(habit);
        System.out.println("Введите данные новой привычки");

        HabitInfoDto updatedHabit = promptHabitInfo();
        updatedHabit.setId(habit.getId());
        habitService.updateHabit(updatedHabit, authorizationService.getCurrentUserId());

        System.out.println("Привычка обновлена");
    }

    private void deleteHabit() {
        System.out.println("Удалить привычку");
        HabitInfoDto habit = promptForHabit();

        System.out.println(habit);
        String isSure = prompt("Удалить привычку? (1 - Да, 2 - Нет): ", "^[1-2]$");
        if (isSure.equals("1")) {
            habitService.delete(habit.getId(), authorizationService.getCurrentUserId());
        }
    }

    private void markHabitAsFulfilled() {
        HabitInfoDto habitInfoDto = promptForHabit();
        System.out.println(habitInfoDto);
        habitService.markHabitAsFulfilled(habitInfoDto.getId(), LocalDate.now(), authorizationService.getCurrentUserId());
        System.out.println("Отметка о выполнении добавлена");
    }

    private void fulfillingReport() {
        HabitInfoDto habit = promptForHabit();

        System.out.println("Введите период статистики:");
        System.out.println("1. День");
        System.out.println("2. Неделя");
        System.out.println("3. Месяц");

        String choice = prompt(" -> ", "^[1-3]$");
        LocalDate startDate = switch (choice) {
            case "1" -> LocalDate.now().minusDays(1);
            case "2" -> LocalDate.now().minusWeeks(1);
            case "3" -> LocalDate.now().minusMonths(1);
            default -> throw new UnsupportedOperationException("Опция не поддерживается");
        };
        LocalDate endDate = LocalDate.now();

        String report = statisticService.generateHabitReportString(habit.getId(), startDate, endDate);
        System.out.println(report);
    }

}
