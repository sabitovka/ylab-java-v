package io.sabitovka.model;

import io.sabitovka.persistence.annotation.Column;
import io.sabitovka.persistence.annotation.Table;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "fulfilled_habits")
public class FulfilledHabit {
    @Column(name = "id")
    Long id;

    @Column(name = "habit_id")
    Long habitId;

    @Column(name = "fulfill_date")
    LocalDate fulfillDate;
}
