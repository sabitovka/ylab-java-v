package io.sabitovka.habittracker.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO для получения статистики
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportParamsDto {
    private Long habitId;
    private LocalDate startDate;
    private LocalDate endDate;
}
