package io.sabitovka.controllers;

import io.sabitovka.dto.UserInfoDto;
import io.sabitovka.service.UserService;

import java.util.Scanner;

public class AdminController extends BaseController {
    private final UserService userService;

    public AdminController(Scanner scanner, UserService userService) {
        super(scanner);
        this.userService = userService;
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("""
                    === Администрирование ===
                    1. Получить список активных пользователей
                    2. Заблокировать пользователя
                    3. Получить список заблокированных пользователей
                    4. Назад""");

            String choice = prompt(" -> ", "^[1-4]$");

            try {
                switch (choice) {
                    case "1" -> activeUsers();
                    case "2" -> blockUser();
                    case "3" -> blockedUsers();
                    case "4" -> {
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

    private void activeUsers() {
        userService.getActiveUsers().forEach(System.out::println);
    }

    private void blockedUsers() {
        System.out.println("Список заблокированных пользователей");
        userService.getBlockedUsers().forEach(System.out::println);
    }

    private void blockUser() {
        String userId = prompt("Введите ID пользователя: ", "\\d+");

        UserInfoDto userInfoDto = userService.findById(Long.parseLong(userId));
        System.out.println(userInfoDto);

        String choice = prompt("Заблокировать пользователя? (Y/N): ", "[ynYN]");
        if (choice.equalsIgnoreCase("y")) {
            userService.blockUser(userInfoDto.getId());
        }
    }

}
