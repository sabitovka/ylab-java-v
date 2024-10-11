package io.sabitovka.controllers;

import io.sabitovka.common.Constants;
import io.sabitovka.service.AuthorizationService;
import io.sabitovka.service.HabitService;
import io.sabitovka.service.UserService;

import java.util.Scanner;

public class MainController extends BaseController {
    private final AuthorizationService authorizationService;
    private final UserController userController;
    private final HabitController habitController;

    public MainController(AuthorizationService authorizationService, UserService userService, HabitService habitService) {
        super(new Scanner(System.in));
        this.authorizationService = authorizationService;

        this.userController = new UserController(scanner, userService, authorizationService);
        this.habitController = new HabitController(scanner, habitService, userService, authorizationService);
    }

    @Override
    public void showMenu() {
        System.out.println("Приложение для отслеживания привычек. Задание V-го интенсива по Java разработке.\n");
        while (true) {
            System.out.println("Для продолжения необходимо выполнить вход в систему или регистрацию:");
            System.out.println("1. Войти в систему");
            System.out.println("2. Зарегистрироваться");
            System.out.println("3. Выйти");

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

            authorizationService.register(name, email, password);
        } catch (Exception e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
    }

    private void showMainMenu() {
        while (true) {
            System.out.println("=== Главное меню  ===");
            System.out.println("Выберите действие из меню");
            System.out.println("1. Управление пользователем");
            System.out.println("2. Управление привычками");
            System.out.println("3. Выход из профиля");

            String choice = prompt(" -> ", "^[1-3]$");
            switch (choice) {
                case "1" -> userController.showMenu();
                case "2" -> habitController.showMenu();
                case "3" -> {
                    authorizationService.logout();
                    System.out.println("Выход на главный экран.");
                    return;
                }
                default -> System.out.println("Неверный выбор, попробуйте снова.");
            }
        }
    }
}
