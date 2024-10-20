package io.sabitovka.model;

import io.sabitovka.enums.HabitFrequency;
import io.sabitovka.persistence.annotation.Column;
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
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "frequency")
    private HabitFrequency frequency;

    @Column(name = "created_at")
    private LocalDate createdAt = LocalDate.now();

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "owner_id")
    private Long ownerId;
}
