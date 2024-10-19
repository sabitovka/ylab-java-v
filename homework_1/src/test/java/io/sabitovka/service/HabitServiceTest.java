package io.sabitovka.service;

import io.sabitovka.dto.HabitInfoDto;
import io.sabitovka.enums.HabitFrequency;
import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import io.sabitovka.repository.FulfilledHabitRepository;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.service.impl.HabitServiceImpl;
import io.sabitovka.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FulfilledHabitRepository fulfilledHabitRepository;
    @Mock
    private UserServiceImpl userService;
    @InjectMocks
    private HabitServiceImpl habitService;

    @Test
    public void createHabit_whenValid_shouldCreateHabit() {
        HabitInfoDto habitInfoDto = new HabitInfoDto(null, "habitName", "description", HabitFrequency.DAILY, null, true, 1L);
        when(userRepository.existsById(1L)).thenReturn(true);

        Habit newHabit = new Habit(null, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 1L);
        when(habitRepository.create(any(Habit.class))).thenReturn(newHabit);

        Habit createdHabit = habitService.createHabit(habitInfoDto);

        assertThat(createdHabit.getName()).isEqualTo("habitName");
        verify(habitRepository).create(any(Habit.class));
    }

    @Test
    public void createHabit_whenInvalidOwner_shouldThrowException() {
        HabitInfoDto habitInfoDto = new HabitInfoDto(null, "habitName", "description", HabitFrequency.DAILY, null, true, 999L);
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> habitService.createHabit(habitInfoDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Владелец привычки должен быть валидным пользователем");
    }

    @Test
    public void getHabitsByFilters_shouldReturnFilteredHabits() {
        User currentUser = new User(1L, "user", "user@example.com", "password", false, true);
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 1L);
        when(habitRepository.filterByUserAndTimeAndStatus(currentUser, null, null, true)).thenReturn(List.of(habit));

        List<HabitInfoDto> habitDtos = habitService.getHabitsByFilters(currentUser, null, null, true);

        assertThat(habitDtos).hasSize(1);
        assertThat(habitDtos.get(0).getName()).isEqualTo("habitName");
    }

    @Test
    public void getAllByOwner_shouldReturnHabitsOfUser() {
        User currentUser = new User(1L, "user", "user@example.com", "password", false, true);
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 1L);
        when(habitRepository.findAllByUser(currentUser)).thenReturn(List.of(habit));

        List<HabitInfoDto> habitDtos = habitService.getAllByOwner(currentUser);

        assertThat(habitDtos).hasSize(1);
        assertThat(habitDtos.get(0).getName()).isEqualTo("habitName");
    }

    @Test
    public void disableHabit_shouldSetHabitInactive() {
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 1L);
        habitService.disableHabit(habit);

        assertThat(habit.isActive()).isFalse();
        verify(habitRepository).update(habit);
    }

    @Test
    public void updateHabit_whenValid_shouldUpdateSuccessfully() {
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 1L);
        HabitInfoDto updatedHabit = new HabitInfoDto(1L, "newName", "newDescription", HabitFrequency.WEEKLY, null, true, 1L);

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "user", "user@example.com", "password", false, true)));

        habitService.updateHabit(updatedHabit, 1L);

        assertThat(habit.getName()).isEqualTo("newName");
        assertThat(habit.getDescription()).isEqualTo("newDescription");
        assertThat(habit.getFrequency()).isEqualTo(HabitFrequency.WEEKLY);
        verify(habitRepository).update(habit);
    }

    @Test
    public void delete_shouldDeleteHabitAndFulfilledHabits() {
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 1L);
        FulfilledHabit fulfilledHabit = new FulfilledHabit(1L, 1L, LocalDate.now());

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "user", "user@example.com", "password", false, true)));
        when(fulfilledHabitRepository.findAll()).thenReturn(List.of(fulfilledHabit));

        habitService.delete(1L, 1L);

        verify(habitRepository).deleteById(1L);
        verify(fulfilledHabitRepository).deleteById(1L);
    }

    @Test
    public void markHabitAsFulfilled_shouldCreateFulfilledHabit() {
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 1L);

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "user", "user@example.com", "password", false, true)));

        habitService.markHabitAsFulfilled(1L, LocalDate.now(), 1L);

        verify(fulfilledHabitRepository).create(any(FulfilledHabit.class));
    }
}
