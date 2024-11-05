package io.sabitovka.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public abstract class BaseController {
    protected Scanner scanner;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public abstract void showMenu();

    public BaseController(Scanner scanner) {
        this.scanner = scanner;
    }

    protected String prompt(String promptText, String regex) {
        String input;
        while (true) {
            System.out.print(promptText);
            input = scanner.nextLine();
            if (input.matches(regex)) {
                return input;
            } else {
                System.out.println("Некорректный ввод, попробуйте снова.");
            }
        }
    }

    protected LocalDate promptForDate(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }
        return LocalDate.parse(input, dateFormatter);
    }

    protected boolean promptForBoolean(String message) {
        String value = prompt(message, "^[1-2]$");
        return value.equals("1");
    }
}
