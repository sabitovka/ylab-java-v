package io.sabitovka.controllers;

import io.sabitovka.common.Constants;
import io.sabitovka.dto.UserInfoDto;
import io.sabitovka.service.AuthorizationService;
import io.sabitovka.service.UserService;
import io.sabitovka.service.impl.AuthorizationServiceImpl;
import io.sabitovka.service.impl.UserServiceImpl;

import java.util.Scanner;

public class UserController extends BaseController {
    private final UserService userService;
    private final AuthorizationService authorizationService;

    public UserController(Scanner scanner, UserService userService, AuthorizationService authorizationService) {
        super(scanner);
        this.userService = userService;
        this.authorizationService = authorizationService;
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.printf("""
                    === Редактирование профиля пользователя ===
                    Текущие параметры пользователя: %s
                    Выберите действие из меню
                    1. Изменить имя
                    2. Изменить email
                    3. Изменить пароль
                    4. Удалить профиль
                    5. Назад
                    """, userService.mapUserToUserInfo(authorizationService.getCurrentUser()));

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
        String newName = prompt("Введите новое имя: ", Constants.USERNAME_REGEX);

        UserInfoDto userInfoDto = userService.mapUserToUserInfo(authorizationService.getCurrentUser());
        userInfoDto.setName(newName);

        userService.updateUser(userInfoDto);
        System.out.println("Имя пользователя успешно изменено на: " + newName.trim());
    }

    private void changeEmail() {
        String newEmail = prompt("Введите новый email: ", Constants.EMAIL_REGEX);

        UserInfoDto userInfoDto = userService.mapUserToUserInfo(authorizationService.getCurrentUser());
        userInfoDto.setEmail(newEmail);

        userService.updateUser(userInfoDto);

        System.out.println("Email успешно изменен на: " + newEmail.trim());
    }

    private void changePassword() {
        String oldPassword = prompt("Введите старый пароль: ", Constants.PASSWORD_REGEX);
        String newPassword = prompt("Введите новый пароль: ", Constants.PASSWORD_REGEX);

        UserInfoDto userInfoDto = userService.mapUserToUserInfo(authorizationService.getCurrentUser());
        userInfoDto.setPassword(newPassword);

        userService.changePassword(userInfoDto, oldPassword);

        System.out.println("Пароль успешно изменен");
    }

    private boolean deleteProfile() {
        String answer = prompt("Вы действительно хотите удалить профиль? (Y/N): ", "^[YNyn]$");
        if (answer.equalsIgnoreCase("y")) {
            String password = prompt("Введите пароль от аккаунта: ", Constants.PASSWORD_REGEX);

            userService.deleteProfile(authorizationService.getCurrentUserId(), password);

            System.out.println("Профиль удален. Надеемся увидеть Вас снова!");
            return true;
        }
        return false;
    }
}
