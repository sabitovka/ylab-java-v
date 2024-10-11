package io.sabitovka.dto;

import java.time.LocalDate;
import java.time.Period;

public class HabitInfoDto {
    private Long id;
    private String name;
    private String description;
    private Period frequency;
    private LocalDate createdAt;
    private boolean isActive;
    private String owner;

    public HabitInfoDto(String name, String description, Period frequency) {
        this(null, name, description, frequency);
    }

    public HabitInfoDto(Long id, String name, String description, Period frequency) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
