package io.sabitovka.controllers;

import io.sabitovka.common.Constants;
import io.sabitovka.dto.UserInfoDto;
import io.sabitovka.service.AuthorizationService;
import io.sabitovka.service.HabitService;
import io.sabitovka.service.StatisticService;
import io.sabitovka.service.UserService;

import java.util.Scanner;

public class MainController extends BaseController {
    private final AuthorizationService authorizationService;
    private final UserService userService;
    private final UserController userController;
    private final HabitController habitController;
    private final AdminController adminController;

    public MainController(AuthorizationService authorizationService, UserService userService, StatisticService statisticService, HabitService habitService) {
        super(new Scanner(System.in));
        this.authorizationService = authorizationService;
        this.userService = userService;

        this.userController = new UserController(scanner, userService, authorizationService);
        this.habitController = new HabitController(scanner, habitService, authorizationService, statisticService);
        this.adminController = new AdminController(scanner, userService);
    }

    @Override
    public void showMenu() {
        System.out.println("Приложение для отслеживания привычек. Задание V-го интенсива по Java разработке.\n");
        while (true) {
            System.out.println("""
                    Для продолжения необходимо выполнить вход в систему или регистрацию:
                    1. Войти в систему
                    2. Зарегистрироваться
                    3. Выйти""");

            String choice = prompt(" -> ", "^[1-3]$");
            switch (choice) {
                case "1" -> {
                    if (login()) {
                        System.out.println("Здравствуйте, " + authorizationService.getCurrentUser().getName());
                        showMainMenu();
                    }
                }
                case "2" -> register();
                case "3" -> {
                    System.out.println("Выход из программы. До свидания!");
                    return;
                }
                default -> System.out.println("Неверный выбор, попробуйте снова.");
            }
        }
    }

    private boolean login() {
        try {
            System.out.println("=== Вход в систему ===");
            String email = prompt("Введите email: ", Constants.EMAIL_REGEX);
            String password = prompt("Введите пароль: ", Constants.PASSWORD_REGEX);

            authorizationService.login(email, password);
            if (authorizationService.isLoggedIn()) {
                System.out.println("Вход успешен! Добро пожаловать.\n");
                return true;
            }  else {
                System.out.println("Неверный логин или пароль, попробуйте снова");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
        return false;
    }

    private void register() {
        try {
            System.out.println("=== Регистрация ===");
            String name = prompt("Введите имя", Constants.USERNAME_REGEX);
            String email = prompt("Введите email: ", Constants.EMAIL_REGEX);
            String password = prompt("Введите пароль: ", Constants.PASSWORD_REGEX);

            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setName(name);
            userInfoDto.setEmail(email);
            userInfoDto.setPassword(password);

            userService.createUser(userInfoDto);
        } catch (Exception e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
    }

    private void showMainMenu() {
        while (true) {
            System.out.println("""
                    === Главное меню  ===
                    Выберите действие из меню
                    1. Управление пользователем
                    2. Управление привычками
                    3. Выход из профиля""");

            String choiceRegex = "^[1-3]$";
            if (authorizationService.isAdmin()) {
                System.out.println("4. Администрирование");
                choiceRegex = "^[1-4]$";
            }

            String choice = prompt(" -> ", choiceRegex);
            switch (choice) {
                case "1" -> userController.showMenu();
                case "2" -> habitController.showMenu();
                case "3" -> {
                    authorizationService.logout();
                    System.out.println("Выход на главный экран.");
                    return;
                }
                case "4" -> adminController.showMenu();
                default -> System.out.println("Неверный выбор, попробуйте снова.");
            }
        }
    }
}
