package io.sabitovka.controllers;

import io.sabitovka.service.StatisticService;

import java.time.LocalDate;
import java.time.Period;
import java.util.Scanner;

public class StatisticController extends BaseController {

    private final StatisticService statisticService;

    public StatisticController(Scanner scanner, StatisticService statisticService) {
        super(scanner);
        this.statisticService = statisticService;
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("=== Статистика и аналитика ===");
            System.out.println("1. Получить статистику выполнения привычки за период");
            System.out.println("2. Подсчет текущих серий выполнения привычки (streak)");
            System.out.println("3. Процент успешного выполнения привычки за определенный период");
            System.out.println("4. Формирование отчета по прогрессу выполнения привычки");
            System.out.println();
            System.out.println("5. Назад");

            String choice = prompt(" -> ", "^[1-5]$");

            try {
                switch (choice) {
                    case "1" -> fulfillingStatistic();
                    case "2" -> {}
                    case "3" -> {}
                    case "4" -> fulfillingReport();
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

    private StatisticData promptForHabitIdAndPeriod() {
        String habitId = prompt("Введите ID привычки: ", "\\d+");

        StatisticData statisticData = new StatisticData();
        statisticData.habitId = Long.valueOf(habitId);
        statisticData.endDate = LocalDate.now();

        System.out.println("Введите период статистики:");
        System.out.println("1. День");
        System.out.println("2. Неделя");
        System.out.println("3. Месяц");


        String choice = prompt(" -> ", "^[1-3]$");
        switch (choice) {
            case "1" -> statisticData.startDate = LocalDate.now().minusDays(1);
            case "2" -> statisticData.startDate = LocalDate.now().minusWeeks(1);
            case "3" -> statisticData.startDate = LocalDate.now().minusMonths(1);
            default -> throw new UnsupportedOperationException("Опция не поддерживается");
        }

        return statisticData;
    }

    private void fulfillingStatistic() {
        StatisticData statisticData = promptForHabitIdAndPeriod();

        statisticService.getHabitCompletionStats(statisticData.habitId, statisticData.startDate, statisticData.endDate);
    }

    private void fulfillingReport() {
        String habitId = prompt("Введите ID привычки: ", "\\d+");
        String report = statisticService.generateHabitReportString(Long.parseLong(habitId), LocalDate.now().minusMonths(1), LocalDate.now());
        System.out.println(report);
    }

    private static final class StatisticData {
        Long habitId;
        LocalDate startDate;
        LocalDate endDate;
    }
}
