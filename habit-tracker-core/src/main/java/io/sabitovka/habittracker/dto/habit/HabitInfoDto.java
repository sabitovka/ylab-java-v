package io.sabitovka.habittracker.dto.habit;

import io.sabitovka.habittracker.enums.HabitFrequency;
import io.sabitovka.habittracker.util.validation.annotation.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Информация о привычке
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HabitInfoDto {
    private Long id;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private HabitFrequency frequency;

    @NotNull
    private LocalDate createdAt;

    private boolean isActive;

    @NotNull
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
