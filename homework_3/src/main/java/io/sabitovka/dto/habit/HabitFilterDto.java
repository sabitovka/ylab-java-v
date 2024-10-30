package io.sabitovka.dto.habit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class HabitFilterDto {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
}
