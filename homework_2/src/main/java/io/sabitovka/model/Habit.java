package io.sabitovka.model;

import io.sabitovka.enums.HabitFrequency;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Habit {
    private Long id;
    private String name;
    private String description;
    private HabitFrequency frequency;
    private LocalDate createdAt;
    private boolean isActive;
    private Long ownerId;
}
