package io.sabitovka.service;

import java.time.LocalDate;
import java.util.Map;

public interface StatisticService {
    Map<LocalDate, Boolean> getHabitCompletionStats(Long habitId, LocalDate startDate, LocalDate endDate);
    int getStreakCount(Long habitId, LocalDate currentDate);
    double getHabitSuccessRate(Long habitId, LocalDate startDate, LocalDate endDate);
    String generateHabitReportString(Long habitId, LocalDate startDate, LocalDate endDate);
}
