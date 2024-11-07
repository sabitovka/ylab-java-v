package io.sabitovka.habittracker.service;

import java.time.LocalDate;
import java.util.Map;

/**
 * Сервис для получения статистики выполнения привычек, отчета по прогрессу выполнения и аналитики
 */
public interface StatisticService {
    /**
     * Получить статистику выполнения привычки за определенный период
     * @param habitId Привычка, по которой нужно получить статистику
     * @param startDate Дата начала отчета
     * @param endDate Дата окончания отчета
     * @return {@link Map}, у которой ключ один день в периоде отчета, значение - отметка о выполнении привычки {@code true}
     */
    Map<LocalDate, Boolean> getHabitCompletionStats(Long habitId, LocalDate startDate, LocalDate endDate);

    /**
     * Возвращает текущий streak привычки. Количество подряд выполненных привычек на указанный момент.
     * Если привычка выполняется раз в неделю, то streak, равный 4 будет считаться, когда привычка выполнялась каждую неделю в месяце
     * @param habitId Привычка, по которой нужно получить streak
     * @param currentDate Дата, на какой момент нужно получить streak
     * @return Количество подряд выполненных привычек
     */
    int getStreakCount(Long habitId, LocalDate currentDate);

    /**
     * Возвращает процент успешно выполненных привычек за определенный период
     * Процент считается как количество выполненных привычек к количеству невыполненных в рамках указанного периода
     * @param habitId ID привычки, по которой нужно получить отчет
     * @param startDate Дата начала отчета
     * @param endDate Дата конца периода
     * @return Процент успешного выполнения
     */
    double getHabitSuccessRate(Long habitId, LocalDate startDate, LocalDate endDate);

    /**
     * Генерирует отчет о выполнении и прогрессе выполнения привычки
     * @param habitId ID привычки
     * @param startDate Дата начала отчета
     * @param endDate Дата конца отчета
     * @return Строку с отчетом
     */
    String generateHabitReportString(Long habitId, LocalDate startDate, LocalDate endDate);
}
