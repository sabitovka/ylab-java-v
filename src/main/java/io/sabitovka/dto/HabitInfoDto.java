package io.sabitovka.dto;

import io.sabitovka.model.User;

import java.time.LocalDate;
import java.time.Period;

public class HabitInfoDto {
    private Long id;
    private String name;
    private String description;
    private Period frequency;
    private LocalDate createdAt;
    private boolean isActive;
    private UserInfoDto owner;

    public HabitInfoDto(String name, String description, Period frequency, UserInfoDto owner) {
        this(null, name, description, frequency, owner);
    }

    public HabitInfoDto(Long id, String name, String description, Period frequency, UserInfoDto owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.owner = owner;
    }

    public HabitInfoDto(Long id, String name, String description, Period frequency, LocalDate createdAt, boolean isActive, UserInfoDto owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.createdAt = createdAt;
        this.isActive = isActive;
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

    public UserInfoDto getOwner() {
        return owner;
    }

    public void setOwner(UserInfoDto owner) {
        this.owner = owner;
    }

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
