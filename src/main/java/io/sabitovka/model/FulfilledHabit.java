package io.sabitovka.model;

import java.time.LocalDate;

public class FulfilledHabit {
    Long id;
    Habit habit;
    LocalDate fulfillDate;

    public FulfilledHabit(FulfilledHabit fulfilledHabit) {
        this(fulfilledHabit.id, fulfilledHabit.habit, fulfilledHabit.fulfillDate);
    }

    public FulfilledHabit(Long id, Habit habit, LocalDate fulfillDate) {
        this.id = id;
        this.habit = habit;
        this.fulfillDate = fulfillDate;
    }
}
