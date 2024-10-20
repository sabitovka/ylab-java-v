package io.sabitovka.model;

import io.sabitovka.persistence.annotation.Column;
import io.sabitovka.persistence.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
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
