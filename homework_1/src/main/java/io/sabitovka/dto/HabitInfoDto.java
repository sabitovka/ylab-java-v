package io.sabitovka.dto;

import io.sabitovka.enums.HabitFrequency;

import java.time.LocalDate;

public class HabitInfoDto {
    private Long id;
    private String name;
    private String description;
    private HabitFrequency frequency;
    private LocalDate createdAt;
    private boolean isActive;
    private Long ownerId;

    public HabitInfoDto() {}

    public HabitInfoDto(String name, String description, HabitFrequency frequency, Long ownerId) {
        this(null, name, description, frequency, ownerId);
    }

    public HabitInfoDto(Long id, String name, String description, HabitFrequency frequency, Long ownerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.ownerId = ownerId;
    }

    public HabitInfoDto(Long id, String name, String description, HabitFrequency frequency, LocalDate createdAt, boolean isActive, Long ownerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.createdAt = createdAt;
        this.isActive = isActive;
        this.ownerId = ownerId;
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

    public HabitFrequency getFrequency() {
        return frequency;
    }

    public boolean isActive() {
        return isActive;
    }

    public Long getOwnerId() {
        return ownerId;
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
