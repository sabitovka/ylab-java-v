package io.sabitovka.controllers;

import io.sabitovka.common.Constants;
import io.sabitovka.dto.HabitInfoDto;
import io.sabitovka.model.Habit;
import io.sabitovka.service.HabitService;
import io.sabitovka.service.UserService;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Scanner;

public class HabitController extends BaseController {

    private final HabitService habitService;
    private final UserService userService;

    public HabitController(Scanner scanner, HabitService habitService, UserService userService) {
        super(scanner);
        this.habitService = habitService;
        this.userService = userService;
    }

    public void showMenu() {
        while (true) {
            System.out.println("=== Редактирование привычек ===");
            System.out.println("Выберите действие из меню");
            System.out.println("1. Добавить привычку");
            System.out.println("2. Изменить привычку");
            System.out.println("3. Удалить привычку");
            System.out.println("4. Просмотреть привычки");
            System.out.println("5. Назад");

            String choice = prompt(" -> ", "^[1-5]$");
            try {
                switch (choice) {
                    case "1" -> createHabit();
                    case "2" -> changeHabit();
                    case "3" -> {}
                    case "4" -> printAllHabits();
                    case "5" -> {
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

    private HabitInfoDto fillHabitInfo() {
        String name = prompt("Введите название привычки: ", Constants.HABIT_NAME);
        String description = prompt("Введите описание привычки: ", ".{0,255}");
        System.out.println("Введите частоту привычки: 1. Ежедневно, 2. Еженедельно");
        String frequencyChoice = prompt(" -> ", "^[1-2]$");
        Period frequency = switch (frequencyChoice) {
            case "1" -> Period.ofDays(1);
            case "2" -> Period.ofWeeks(1);
            default -> throw new IllegalStateException("Unexpected value: " + frequencyChoice);
        };

        return new HabitInfoDto(name, description, frequency);
    }

    public void createHabit() {
        System.out.println("Добавление привычки");
        HabitInfoDto habitInfoDto = fillHabitInfo();

        habitService.createHabit(habitInfoDto, userService.getCurrentUser());
        System.out.println("Новая привычка добавлена");
    }

    public void printAllHabits() {
        System.out.println("== Список всех привычек ==");
        boolean useFilter = promptForBoolean("Применить фильтрацию? (1 - Да, 2 - Нет): ");
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

            List<Habit> filteredHabits = habitService.getHabitsByFilters(userService.getCurrentUser(), startDate, endDate, activeFilter);
            System.out.println("Найденные привычки:");
            filteredHabits.forEach(habit -> System.out.println(habit.toString()));
        } else {
            List<Habit> allHabits = habitService.getAllByOwner(userService.getCurrentUser());
            System.out.println("Найденные привычки:");
            allHabits.forEach(habit -> System.out.println(habit.toString()));
        }
    }

    public void changeHabit() {
        System.out.println("Изменить привычку");
        String habitId = prompt("Введите ID привычки: ", "\\d+");
        Habit habit = habitService.getHabitById(Long.parseLong(habitId));
        System.out.println(habit);
        System.out.println("Введите данные новой привычки");
        HabitInfoDto updatedHabit = fillHabitInfo();
    }

}
