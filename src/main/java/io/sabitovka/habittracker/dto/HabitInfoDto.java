package io.sabitovka.habittracker.dto;

import io.sabitovka.habittracker.enums.HabitFrequency;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HabitInfoDto {
    private Long id;
    private String name;
    private String description;
    private HabitFrequency frequency;
    private LocalDate createdAt;
    private boolean isActive;
    private Long ownerId;

    @Override
    public String toString() {
        return "Привычка #" + id +
                ", Название: " + name +
                ", Описание: " + description +
                ", Периодичность: " + frequency +
                ", Создана: " + createdAt +
                ", Активна: " + (isActive ? "Да" : "Нет");
    }

}
