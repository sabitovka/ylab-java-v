package io.sabitovka.dto;

import io.sabitovka.enums.HabitFrequency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
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
