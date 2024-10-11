package io.sabitovka.model;

import io.sabitovka.service.HabitService;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class Habit {
    private Long id;
    private String name;
    private String description;
    private Period frequency;
    private LocalDate createdAt;
    private boolean isActive;
    private User owner;

    public Habit(String name, String description, Period frequency, User owner) {
        this(null, name, description, frequency, owner);
    }

    public Habit(Habit habit) {
        this(habit.id, habit.name, habit.description, habit.frequency, habit.owner);
        this.setActive(habit.isActive());
        this.setCreatedAt(habit.getCreatedAt());
    }

    public Habit(Long id, String name, String description, Period frequency, User owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.createdAt = LocalDate.now();
        this.isActive = true;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Period getFrequency() {
        return frequency;
    }

    public void setFrequency(Period frequency) {
        this.frequency = frequency;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Habit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", frequency=" + frequency +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                ", owner=" + owner +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Habit habit = (Habit) o;
        return isActive == habit.isActive && Objects.equals(id, habit.id) && Objects.equals(name, habit.name) && Objects.equals(description, habit.description) && Objects.equals(frequency, habit.frequency) && Objects.equals(createdAt, habit.createdAt) && Objects.equals(owner, habit.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, frequency, createdAt, isActive, owner);
    }
}
