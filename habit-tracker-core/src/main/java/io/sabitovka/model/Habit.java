package io.sabitovka.model;

import io.sabitovka.enums.HabitFrequency;
import io.sabitovka.persistence.annotation.Column;
import io.sabitovka.persistence.annotation.Table;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "habits")
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