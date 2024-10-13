package io.sabitovka.service;

import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.model.Habit;
import io.sabitovka.repository.FulfilledHabitRepository;
import io.sabitovka.repository.HabitRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticService {

    private final HabitRepository habitRepository;
    private final FulfilledHabitRepository fulfilledHabitRepository;

    public StatisticService(HabitRepository habitRepository, FulfilledHabitRepository fulfilledHabitRepository) {
        this.habitRepository = habitRepository;
        this.fulfilledHabitRepository = fulfilledHabitRepository;
    }

    public Map<LocalDate, Boolean> getHabitCompletionStats(Long habitId, LocalDate startDate, LocalDate endDate) {
        if (!habitRepository.existsById(habitId)) {
            throw new EntityNotFoundException("Не удалось найти привычку с ID=" + habitId);
        }

        List<FulfilledHabit> fulfilledHabits = fulfilledHabitRepository.findAll().stream()
                .filter(fulfilledHabit -> fulfilledHabit.getHabitId().equals(habitId))
                .filter(fulfilledHabit -> fulfilledHabit.getFulfillDate().isAfter(startDate) && fulfilledHabit.getFulfillDate().isBefore(endDate))
                .toList();

        Map<LocalDate, Boolean> completionStats = new HashMap<>();
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            final LocalDate eqDate = date;
            boolean isFulfilled = fulfilledHabits.stream().anyMatch(fulfilledHabit -> fulfilledHabit.getFulfillDate().equals(eqDate));
            completionStats.put(date, isFulfilled);
        }

        return completionStats;
    }

    public int getStreakCount(Long habitId, LocalDate currentDate) {
        if (!habitRepository.existsById(habitId)) {
            throw new EntityNotFoundException("Не удалось найти привычку с ID=" + habitId);
        }

        List<FulfilledHabit> fulfilledHabits = fulfilledHabitRepository.findAll().stream()
                .filter(fulfilledHabit -> fulfilledHabit.getHabitId().equals(habitId))
                .filter(fulfilledHabit -> fulfilledHabit.getFulfillDate().isBefore(currentDate))
                .sorted(Comparator.comparing(FulfilledHabit::getFulfillDate))
                .toList();

        int streak = 0;
        LocalDate date = currentDate;
        for (FulfilledHabit habit: fulfilledHabits) {
            if (habit.getFulfillDate().equals(date)) {
                streak++;
                date = date.plusDays(streak);
            } else {
                break;
            }
        }

        return streak;
    }

    public double getHabitSuccessRate(Long habitId, LocalDate startDate, LocalDate endDate) {
        if (!habitRepository.existsById(habitId)) {
            throw new EntityNotFoundException("Не удалось найти привычку с ID=" + habitId);
        }

        Map<LocalDate, Boolean> stats = getHabitCompletionStats(habitId, startDate, endDate);
        long successDays = stats.values().stream().filter(Boolean::booleanValue).count();
        long totalDays = stats.size();
        return (double) successDays / totalDays * 100;
    }

    public String generateHabitReportString(Long habitId, LocalDate startDate, LocalDate endDate) {
        Habit habit = habitRepository.findById(habitId).orElseThrow(() -> new EntityNotFoundException("Не удалось найти привычку с ID=" + habitId));

        int streak = getStreakCount(habitId, endDate);
        double successRate = getHabitSuccessRate(habitId, startDate, endDate);
        Map<LocalDate, Boolean> completionStats = getHabitCompletionStats(habitId, startDate, endDate);

        StringBuilder report = new StringBuilder();
        report.append("Отчет по привычке ").append(habit.getName()).append(":\n");
        report.append("Период: ").append(startDate).append(" - ").append(endDate).append("\n");
        report.append("Текущая серия: ").append(streak).append("\n");
        report.append("Процент успешности: ").append(String.format("%.2f", successRate)).append("%\n");
        report.append("История выполнения:").append("\n");

        completionStats.forEach((date, completed) -> {
            report.append(date).append(" - ").append(completed ? "Выполнено" : "Не выполнено").append("\n");
        });

        return report.toString();
    }
}
