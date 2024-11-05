package io.sabitovka.service.impl;

import io.sabitovka.enums.HabitFrequency;
import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.model.Habit;
import io.sabitovka.repository.FulfilledHabitRepository;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.service.StatisticService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис для управления статистикой выполнения привычки. Реализует интерфейс {@link StatisticService}
 */
public class StatisticServiceImpl implements StatisticService {
    private final HabitRepository habitRepository;
    private final FulfilledHabitRepository fulfilledHabitRepository;

    public StatisticServiceImpl(HabitRepository habitRepository, FulfilledHabitRepository fulfilledHabitRepository) {
        this.habitRepository = habitRepository;
        this.fulfilledHabitRepository = fulfilledHabitRepository;
    }

    private LocalDate incDate(HabitFrequency habitFrequency, LocalDate date) {
        return switch (habitFrequency) {
            case DAILY -> date.plusDays(1);
            case WEEKLY -> date.plusWeeks(1);
        };
    }

    private LocalDate subDate(HabitFrequency habitFrequency, LocalDate date) {
        return switch (habitFrequency) {
            case DAILY -> date.minusDays(1);
            case WEEKLY -> date.minusWeeks(1);
        };
    }

    public Map<LocalDate, Boolean> getHabitCompletionStats(Long habitId, LocalDate startDate, LocalDate endDate) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Не удалось найти привычку с ID=" + habitId));

        List<FulfilledHabit> fulfilledHabits = fulfilledHabitRepository.findAll().stream()
                .filter(fulfilledHabit -> fulfilledHabit.getHabitId().equals(habitId))
                .filter(fulfilledHabit -> !fulfilledHabit.getFulfillDate().isBefore(startDate) && !fulfilledHabit.getFulfillDate().isAfter(endDate))
                .toList();

        Map<LocalDate, Boolean> completionStats = new HashMap<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = incDate(habit.getFrequency(), date)) {
            final LocalDate eqDate = date;
            boolean isFulfilled = fulfilledHabits.stream().anyMatch(fulfilledHabit -> fulfilledHabit.getFulfillDate().equals(eqDate));
            completionStats.put(date, isFulfilled);
        }

        return completionStats;
    }

    public int getStreakCount(Long habitId, LocalDate currentDate) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Не удалось найти привычку с ID=" + habitId));

        List<FulfilledHabit> fulfilledHabits = fulfilledHabitRepository.findAll().stream()
                .filter(fulfilledHabit -> fulfilledHabit.getHabitId().equals(habitId))
                .filter(fulfilledHabit -> !fulfilledHabit.getFulfillDate().isAfter(currentDate))
                .sorted(Comparator.comparing(FulfilledHabit::getFulfillDate).reversed())
                .toList();

        int streak = 0;
        LocalDate date = currentDate;
        for (FulfilledHabit fulfilledHabit: fulfilledHabits) {
            if (fulfilledHabit.getFulfillDate().equals(date)) {
                streak++;
                date = subDate(habit.getFrequency(), date);
            } else {
                break;
            }
        }

        return streak;
    }

    public double getHabitSuccessRate(Long habitId, LocalDate startDate, LocalDate endDate) {
        if (!habitRepository.existsById(habitId)) {
            throw new IllegalArgumentException("Не удалось найти привычку с ID=" + habitId);
        }

        Map<LocalDate, Boolean> stats = getHabitCompletionStats(habitId, startDate, endDate);
        long successDays = stats.values().stream().filter(Boolean::booleanValue).count();
        long totalDays = stats.size();
        return (double) successDays / totalDays * 100;
    }

    public String generateHabitReportString(Long habitId, LocalDate startDate, LocalDate endDate) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Не удалось найти привычку с ID=" + habitId));

        int streak = getStreakCount(habitId, endDate);
        double successRate = getHabitSuccessRate(habitId, startDate, endDate);
        Map<LocalDate, Boolean> completionStats = getHabitCompletionStats(habitId, startDate, endDate);

        StringBuilder report = new StringBuilder();
        report.append("Отчет по привычке ").append(habit.getName()).append(":\n");
        report.append("Период: ").append(startDate).append(" - ").append(endDate).append("\n");
        report.append("Текущая серия: ").append(streak).append("\n");
        report.append("Процент успешности: ").append(String.format("%.2f", successRate)).append("%\n");
        report.append("История выполнения:").append("\n");

        completionStats.forEach((date, completed) ->
                report.append(date).append(" - ").append(completed ? "Выполнено" : "Не выполнено").append("\n"));

        return report.toString();
    }
}
