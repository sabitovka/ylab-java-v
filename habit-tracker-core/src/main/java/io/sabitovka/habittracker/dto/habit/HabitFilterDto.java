package io.sabitovka.habittracker.dto.habit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO для выполнения запроса получения привычек по фильтру
 */
@Getter
@Setter
@NoArgsConstructor
public class HabitFilterDto {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
}
