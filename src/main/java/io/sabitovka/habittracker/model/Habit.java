package io.sabitovka.habittracker.model;

import io.sabitovka.habittracker.enums.HabitFrequency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Habit {
    private Long id;
    private String name;
    private String description;
    private HabitFrequency frequency;
    private LocalDate createdAt = LocalDate.now();
    private boolean isActive = true;
    private Long ownerId;
}
