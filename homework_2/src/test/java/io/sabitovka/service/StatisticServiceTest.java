package io.sabitovka.service;

import io.sabitovka.enums.HabitFrequency;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.model.Habit;
import io.sabitovka.repository.FulfilledHabitRepository;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.service.impl.StatisticServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест сервиса StatisticServiceImpl")
class StatisticServiceTest {
    @Mock
    private HabitRepository habitRepository;
    @Mock
    private FulfilledHabitRepository fulfilledHabitRepository;
    @InjectMocks
    private StatisticServiceImpl statisticService;

    @Test
    @DisplayName("[getHabitCompletionStats] Должен вернуть статистику выполнения")
    public void getHabitCompletionStats_whenValidHabit_shouldReturnCompletionStats() {
        Habit habit = new Habit();
        habit.setFrequency(HabitFrequency.DAILY);
        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));

        FulfilledHabit fulfilledHabit = new FulfilledHabit(1L, 1L, LocalDate.now().minusDays(2));
        when(fulfilledHabitRepository.findAll()).thenReturn(List.of(fulfilledHabit));

        Map<LocalDate, Boolean> stats = statisticService.getHabitCompletionStats(1L, LocalDate.now().minusDays(5), LocalDate.now());

        assertThat(stats.get(LocalDate.now())).isFalse();
        assertThat(stats.get(LocalDate.now().minusDays(1))).isFalse();
        assertThat(stats.get(LocalDate.now().minusDays(2))).isTrue();
        assertThat(stats.get(LocalDate.now().minusDays(3))).isFalse();
        assertThat(stats.get(LocalDate.now().minusDays(4))).isFalse();
    }

    @Test
    @DisplayName("[getStreakCount] Должен вернуть текущий streak")
    public void getStreakCount_whenValidHabit_shouldReturnCorrectStreak() {
        Habit habit = new Habit();
        habit.setFrequency(HabitFrequency.DAILY);
        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));

        FulfilledHabit fulfilledHabit1 = new FulfilledHabit(1L, 1L, LocalDate.of(2024, 10, 12));
        FulfilledHabit fulfilledHabit2 = new FulfilledHabit(2L, 1L, LocalDate.of(2024, 10, 13));
        when(fulfilledHabitRepository.findAll()).thenReturn(List.of(fulfilledHabit1, fulfilledHabit2));

        int streak = statisticService.getStreakCount(1L, LocalDate.of(2024, 10, 13));

        assertThat(streak).isEqualTo(2);
    }

    @Test
    @DisplayName("[getHabitSuccessRate] Должен вернуть корректный процент выполнения")
    public void getHabitSuccessRate_whenValidHabit_shouldReturnCorrectRate() {
        Habit habit = new Habit();
        habit.setFrequency(HabitFrequency.DAILY);
        when(habitRepository.existsById(1L)).thenReturn(true);
        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));

        FulfilledHabit fulfilledHabit1 = new FulfilledHabit(1L, 1L, LocalDate.of(2024, 10, 12));
        when(fulfilledHabitRepository.findAll()).thenReturn(List.of(fulfilledHabit1));

        double successRate = statisticService.getHabitSuccessRate(1L, LocalDate.of(2024, 10, 10), LocalDate.of(2024, 10, 13));

        assertThat(successRate).isEqualTo(25.0);
    }

    @Test
    @DisplayName("[getHabitSuccessRate] Когда привычки нет, должен выбросить исключение")
    public void getHabitSuccessRate_whenHabitDoesNotExist_shouldThrowException() {
        when(habitRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> statisticService.getHabitSuccessRate(1L, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 5)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("[generateHabitReportString] Должен создать отчет о выполнении")
    public void generateHabitReportString_shouldReturnFormattedReport() {
        Habit habit = new Habit(1L, "Test Habit", "description", HabitFrequency.DAILY, LocalDate.now(), false, 1L);
        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(habitRepository.existsById(1L)).thenReturn(true);

        FulfilledHabit fulfilledHabit1 = new FulfilledHabit(1L, 1L, LocalDate.of(2024, 10, 12));
        FulfilledHabit fulfilledHabit2 = new FulfilledHabit(2L, 1L, LocalDate.of(2024, 10, 13));
        when(fulfilledHabitRepository.findAll()).thenReturn(List.of(fulfilledHabit1, fulfilledHabit2));

        String report = statisticService.generateHabitReportString(1L, LocalDate.of(2024, 10, 10), LocalDate.of(2024, 10, 13));

        assertThat(report).contains("Отчет по привычке Test Habit:");
        assertThat(report).contains("Текущая серия: 2");
        assertThat(report).contains("Процент успешности: 50,00%");
        assertThat(report).contains("2024-10-12 - Выполнено");
        assertThat(report).contains("2024-10-11 - Не выполнено");
    }

}
