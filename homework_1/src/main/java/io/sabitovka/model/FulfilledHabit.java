package io.sabitovka.model;

import java.time.LocalDate;

public class FulfilledHabit {
    Long id;
    Long habitId;
    LocalDate fulfillDate;

    public FulfilledHabit() {}

    public FulfilledHabit(FulfilledHabit fulfilledHabit) {
        this(fulfilledHabit.id, fulfilledHabit.habitId, fulfilledHabit.fulfillDate);
    }

    public FulfilledHabit(Long id, Long habitId, LocalDate fulfillDate) {
        this.id = id;
        this.habitId = habitId;
        this.fulfillDate = fulfillDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    public LocalDate getFulfillDate() {
        return fulfillDate;
    }

    public void setFulfillDate(LocalDate fulfillDate) {
        this.fulfillDate = fulfillDate;
    }
}
