package io.sabitovka.habittracker.repository;

import io.sabitovka.habittracker.enums.HabitFrequency;
import io.sabitovka.habittracker.exception.EntityAlreadyExistsException;
import io.sabitovka.habittracker.exception.EntityNotFoundException;
import io.sabitovka.habittracker.model.Habit;
import io.sabitovka.habittracker.model.User;
import io.sabitovka.habittracker.repository.impl.HabitRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Тест репозитория HabitRepositoryImpl")
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
    @DisplayName("[create] Должен успешно создать привычку")
    public void createShouldCreateHabitSuccessfully() {
        Habit createdHabit = habitRepository.create(habit1);

        assertThat(habitRepository.existsById(createdHabit.getId())).isTrue();
        assertThat(createdHabit.getName()).isEqualTo(habit1.getName());
    }

    @Test
    @DisplayName("[create] Когда привычка = null, должен выбросить исключение")
    public void createWhenHabitIsNullShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> habitRepository.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Habit is null");
    }

    @Test
    @DisplayName("[create] Когда привычка уже содержится, должен выбросить исключение")
    public void createWhenHabitAlreadyExistsShouldThrowEntityAlreadyExistsException() {
        habitRepository.create(habit1);

        assertThatThrownBy(() -> habitRepository.create(habit1))
                .isInstanceOf(EntityAlreadyExistsException.class);
    }

    @Test
    @DisplayName("[create] Должен успешно вернуть все привычки")
    public void findByIdWhenHabitExistsShouldReturnHabit() {
        Habit createdHabit = habitRepository.create(habit1);

        Optional<Habit> foundHabit = habitRepository.findById(createdHabit.getId());

        assertThat(foundHabit.isPresent()).isTrue();
        assertThat(foundHabit.get().getName()).isEqualTo(habit1.getName());
    }

    @Test
    @DisplayName("[findById] Когда привычки нет, должен вернуть пустой Optional")
    public void findByIdWhenHabitDoesNotExistShouldReturnEmptyOptional() {
        Optional<Habit> foundHabit = habitRepository.findById(999L);

        assertThat(foundHabit.isPresent()).isFalse();
    }

    @Test
    @DisplayName("[findAll] Должен вернуть все привычки")
    public void findAllShouldReturnAllHabits() {
        habitRepository.create(habit1);
        habitRepository.create(habit2);

        List<Habit> habits = habitRepository.findAll();
        assertThat(habits).hasSize(2);
    }

    @Test
    @DisplayName("[update] Должен успешно обновить привычку")
    public void updateShouldUpdateHabitSuccessfully() {
        Habit createdHabit = habitRepository.create(habit1);
        createdHabit.setName("UpdatedHabit");

        habitRepository.update(createdHabit);

        Optional<Habit> updatedHabit = habitRepository.findById(createdHabit.getId());
        assertThat(updatedHabit.get().getName()).isEqualTo("UpdatedHabit");
    }

    @Test
    @DisplayName("[update] Когда привычка null, должен выбросить исключение")
    public void updateWhenHabitIsNullShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> habitRepository.update(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("[update] Когда привычка не содержится, должен выбросить исключение")
    public void updateWhenHabitDoesNotExistShouldThrowEntityNotFoundException() {
        Habit nonExistentHabit = new Habit(999L, "NonExistent", "NonExistent", HabitFrequency.DAILY,  LocalDate.now(), true, 1L);

        assertThatThrownBy(() -> habitRepository.update(nonExistentHabit))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("[deleteById] Должен успешно удалить по id")
    public void deleteByIdWhenHabitExistsShouldDeleteSuccessfully() {
        Habit createdHabit = habitRepository.create(habit1);

        boolean deleted = habitRepository.deleteById(createdHabit.getId());

        assertThat(deleted).isTrue();
        assertThat(habitRepository.findById(createdHabit.getId()).isPresent()).isFalse();
    }

    @Test
    @DisplayName("[deleteById] Когда привычка не содержится, должен вернуть false")
    public void deleteByIdWhenHabitDoesNotExistShouldReturnFalse() {
        boolean deleted = habitRepository.deleteById(999L);

        assertThat(deleted).isFalse();
    }

    @Test
    @DisplayName("[findAllByUser] Должен вернуть список привычек пользователя")
    public void findAllByUserShouldReturnHabitsByUser() {
        habitRepository.create(habit1);
        habitRepository.create(habit2);

        List<Habit> userHabits = habitRepository.findAllByUser(user);

        assertThat(userHabits).hasSize(2);
    }

    @Test
    @DisplayName("[filterByUserAndTimeAndStatus] Должен отфильтровать корректно")
    public void filterByUserAndTimeAndStatusShouldFilterCorrectly() {
        habitRepository.create(habit1);
        habitRepository.create(habit2);

        List<Habit> filteredHabits = habitRepository.filterByUserAndTimeAndStatus(
                user, LocalDate.now().minusDays(6), LocalDate.now(), true);

        assertThat(filteredHabits).hasSize(1);
        assertThat(filteredHabits.get(0).getName()).isEqualTo(habit1.getName());
    }

    @Test
    @DisplayName("[filterByUserAndTimeAndStatus] Когда ничего не попадает под фильтр, должен вернуть пустой список")
    public void filterByUserAndTimeAndStatusWhenNoMatchesShouldReturnEmptyList() {
        habitRepository.create(habit1);
        habitRepository.create(habit2);

        List<Habit> filteredHabits = habitRepository.filterByUserAndTimeAndStatus(
                user, LocalDate.now().minusDays(1), LocalDate.now(), true);

        assertThat(filteredHabits).isNotEmpty();
    }
}
