package io.sabitovka.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class FulfilledHabit {
    Long id;
    Long habitId;
    LocalDate fulfillDate;
}
