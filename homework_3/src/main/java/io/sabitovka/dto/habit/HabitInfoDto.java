package io.sabitovka.dto.habit;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.sabitovka.enums.HabitFrequency;
import io.sabitovka.util.validation.annotation.Name;
import io.sabitovka.util.validation.annotation.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
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
