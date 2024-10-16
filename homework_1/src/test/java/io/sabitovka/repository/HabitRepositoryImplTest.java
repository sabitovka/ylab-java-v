package io.sabitovka.repository;

import io.sabitovka.enums.HabitFrequency;
import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class HabitRepositoryImplTest {
    private HabitRepositoryImpl habitRepository;
    private User user;
    private Habit habit1;
    private Habit habit2;

    @BeforeEach
    public void setUp() {
        habitRepository = new HabitRepositoryImpl();
        user = new User(1L, "testUser", "test@example.com", "password", false, true);
        habit1 = new Habit(1L, "Habit1", "Description1", HabitFrequency.DAILY, LocalDate.now(), true, 1L);
        habit2 = new Habit(2L, "Habit2", "Description2", HabitFrequency.WEEKLY, LocalDate.now().minusDays(5), false, 1L);
    }

    @Test
    public void create_shouldCreateHabitSuccessfully() {
        Habit createdHabit = habitRepository.create(habit1);
        assertThat(habitRepository.existsById(createdHabit.getId())).isTrue();
        assertThat(createdHabit.getName()).isEqualTo(habit1.getName());
    }

    @Test
    public void create_whenHabitIsNull_shouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> habitRepository.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Habit is null");
    }

    @Test
    public void create_whenHabitAlreadyExists_shouldThrowEntityAlreadyExistsException() {
        habitRepository.create(habit1);
        assertThatThrownBy(() -> habitRepository.create(habit1))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("Привычка уже существует в системе");
    }

    @Test
    public void findById_whenHabitExists_shouldReturnHabit() {
        Habit createdHabit = habitRepository.create(habit1);
        Optional<Habit> foundHabit = habitRepository.findById(createdHabit.getId());
        assertThat(foundHabit.isPresent()).isTrue();
        assertThat(foundHabit.get().getName()).isEqualTo(habit1.getName());
    }

    @Test
    public void findById_whenHabitDoesNotExist_shouldReturnEmptyOptional() {
        Optional<Habit> foundHabit = habitRepository.findById(999L);
        assertThat(foundHabit.isPresent()).isFalse();
    }

    @Test
    public void findAll_shouldReturnAllHabits() {
        habitRepository.create(habit1);
        habitRepository.create(habit2);
        List<Habit> habits = habitRepository.findAll();
        assertThat(habits).hasSize(2);
    }

    @Test
    public void update_shouldUpdateHabitSuccessfully() {
        Habit createdHabit = habitRepository.create(habit1);
        createdHabit.setName("UpdatedHabit");
        habitRepository.update(createdHabit);
        Optional<Habit> updatedHabit = habitRepository.findById(createdHabit.getId());
        assertThat(updatedHabit.get().getName()).isEqualTo("UpdatedHabit");
    }

    @Test
    public void update_whenHabitIsNull_shouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> habitRepository.update(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Habit is null");
    }

    @Test
    public void update_whenHabitDoesNotExist_shouldThrowEntityNotFoundException() {
        Habit nonExistentHabit = new Habit(999L, "NonExistent", "NonExistent", HabitFrequency.DAILY,  LocalDate.now(), true, 1L);
        assertThatThrownBy(() -> habitRepository.update(nonExistentHabit))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Привычка не найдена в системе");
    }

    @Test
    public void deleteById_whenHabitExists_shouldDeleteSuccessfully() {
        Habit createdHabit = habitRepository.create(habit1);
        boolean deleted = habitRepository.deleteById(createdHabit.getId());
        assertThat(deleted).isTrue();
        assertThat(habitRepository.findById(createdHabit.getId()).isPresent()).isFalse();
    }

    @Test
    public void deleteById_whenHabitDoesNotExist_shouldReturnFalse() {
        boolean deleted = habitRepository.deleteById(999L);
        assertThat(deleted).isFalse();
    }

    @Test
    public void findAllByUser_shouldReturnHabitsByUser() {
        habitRepository.create(habit1);
        habitRepository.create(habit2);
        List<Habit> userHabits = habitRepository.findAllByUser(user);
        assertThat(userHabits).hasSize(2);
    }

    @Test
    public void filterByUserAndTimeAndStatus_shouldFilterCorrectly() {
        habitRepository.create(habit1);
        habitRepository.create(habit2);
        List<Habit> filteredHabits = habitRepository.filterByUserAndTimeAndStatus(
                user, LocalDate.now().minusDays(6), LocalDate.now(), true);
        assertThat(filteredHabits).hasSize(1);
        assertThat(filteredHabits.get(0).getName()).isEqualTo(habit1.getName());
    }

    @Test
    public void filterByUserAndTimeAndStatus_whenNoMatches_shouldReturnEmptyList() {
        habitRepository.create(habit1);
        habitRepository.create(habit2);
        List<Habit> filteredHabits = habitRepository.filterByUserAndTimeAndStatus(
                user, LocalDate.now().minusDays(1), LocalDate.now(), true);
        assertThat(filteredHabits).isNotEmpty();
    }
}
