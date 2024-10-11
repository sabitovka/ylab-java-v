package io.sabitovka.controllers;

import io.sabitovka.common.Constants;
import io.sabitovka.service.UserService;

import java.util.Scanner;

public class UserController extends BaseController {

    private final UserService userService;

    public UserController(Scanner scanner, UserService userService) {
        super(scanner);
        this.userService = userService;
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("=== Редактирование профиля пользователя ===");
            System.out.println("Текущие параметры пользователя: " + userService.getCurrentUser().toString());
            System.out.println("Выберите действие из меню");
            System.out.println("1. Изменить имя");
            System.out.println("2. Изменить email");
            System.out.println("3. Изменить пароль");
            System.out.println("4. Удалить профиль");
            System.out.println("5. Назад");

            String choice = prompt(" -> ", "^[1-5]$");
            try {
                switch (choice) {
                    case "1" -> changeName();
                    case "2" -> changeEmail();
                    case "3" -> changePassword();
                    case "4" -> {
                        if (deleteProfile()) {
                            System.exit(0);
                        }
                    }
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

    private void changeName() {
        System.out.println("Введите новое имя");
        String newName = prompt(" -> ", Constants.USERNAME_REGEX);
        userService.changeName(newName);
        System.out.println("Имя пользователя успешно изменено на: " + newName.trim());
    }

    private void changeEmail() {
        System.out.println("Введите новый email");
        String newEmail = prompt(" -> ", Constants.EMAIL_REGEX);
        userService.changeEmail(newEmail);
        System.out.println("Email успешно изменен на: " + newEmail.trim());
    }

    private void changePassword() {
        System.out.println("Введите новый пароль");
        String newPassword = prompt(" -> ", Constants.PASSWORD_REGEX);
        userService.changePassword(newPassword);
        System.out.println("Пароль успешно изменен");
    }

    private boolean deleteProfile() {
        System.out.println("Вы действительно хотите удалить профиль? (Y/N)");
        String answer = prompt(" -> ", "^[Y,N]$");
        if (answer.equalsIgnoreCase("Y")) {
            System.out.print("Введите пароль от аккаунта: ");
            String password = prompt("", Constants.PASSWORD_REGEX);
            userService.deleteProfile(password);
            System.out.println("Профиль удален. Надеемся увидеть Вас снова!");
            return true;
        }
        return false;
    }
}
