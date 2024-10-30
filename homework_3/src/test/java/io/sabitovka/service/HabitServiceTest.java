package io.sabitovka.service;

import io.sabitovka.dto.habit.HabitFilterDto;
import io.sabitovka.dto.habit.HabitInfoDto;
import io.sabitovka.dto.habit.SimpleLocalDateDto;
import io.sabitovka.enums.HabitFrequency;
import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import io.sabitovka.repository.FulfilledHabitRepository;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.service.impl.HabitServiceImpl;
import io.sabitovka.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Тест сервиса HabitServiceImpl")
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
    @DisplayName("[createHabit] Должен создать привычку")
    public void createHabitWhenValidShouldCreateHabit() {
        HabitInfoDto habitInfoDto = new HabitInfoDto(null, "habitName", "description", HabitFrequency.DAILY, null, true, 1L);
        when(userRepository.existsById(1L)).thenReturn(true);

        Habit newHabit = new Habit(null, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 1L);
        when(habitRepository.create(any(Habit.class))).thenReturn(newHabit);

        HabitInfoDto createdHabit = habitService.createHabit(habitInfoDto);

        assertThat(createdHabit.getName()).isEqualTo("habitName");
        verify(habitRepository).create(any(Habit.class));
    }

    @Test
    @DisplayName("[createHabit] Когда пользователь неверный, должен выбросить исключение")
    public void createHabitWhenInvalidOwnerShouldThrowException() {
        HabitInfoDto habitInfoDto = new HabitInfoDto(null, "habitName", "description", HabitFrequency.DAILY, null, true, 999L);
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> habitService.createHabit(habitInfoDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Владелец привычки должен быть валидным пользователем");
    }

    @Test
    @DisplayName("[getHabitsByFilters] Должен вернуть отфильтрованных пользователей")
    public void getHabitsByFiltersShouldReturnFilteredHabits() {
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 1L);
        when(habitRepository.filterByUserAndTimeAndStatus(1L, null, null, true)).thenReturn(List.of(habit));

        HabitFilterDto filterDto = new HabitFilterDto();
        filterDto.setUserId(1L);
        filterDto.setActive(true);
        List<HabitInfoDto> habitDtos = habitService.getHabitsByFilters(filterDto);

        assertThat(habitDtos).hasSize(1);
        assertThat(habitDtos.get(0).getName()).isEqualTo("habitName");
    }

    @Test
    @DisplayName("[getAllByOwner] Должен вернуть привычки пользователя")
    public void getAllByOwnerShouldReturnHabitsOfUser() {
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 1L);
        when(habitRepository.findAllByUserId(1L)).thenReturn(List.of(habit));

        List<HabitInfoDto> habitDtos = habitService.getAllByOwner(1L);

        assertThat(habitDtos).hasSize(1);
        assertThat(habitDtos.get(0).getName()).isEqualTo("habitName");
    }

    @Test
    @DisplayName("[disableHabit] Должен отключить привычку")
    public void disableHabitShouldSetHabitInactive() {
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 1L);
        habitService.disableHabit(1L);

        assertThat(habit.isActive()).isFalse();
        verify(habitRepository).update(habit);
    }

    @Test
    @DisplayName("[updateHabit] Должен обновить привычку")
    public void updateHabitWhenValidShouldUpdateSuccessfully() {
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 1L);
        HabitInfoDto updatedHabit = new HabitInfoDto(1L, "newName", "newDescription", HabitFrequency.WEEKLY, null, true, 1L);

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "user", "user@example.com", "password", false, true)));

        habitService.updateHabit(1L, updatedHabit);

        assertThat(habit.getName()).isEqualTo("newName");
        assertThat(habit.getDescription()).isEqualTo("newDescription");
        assertThat(habit.getFrequency()).isEqualTo(HabitFrequency.WEEKLY);
        verify(habitRepository).update(habit);
    }

    @Test
    @DisplayName("[delete] Должен удалить привычку и историю выполнений")
    public void deleteShouldDeleteHabitAndFulfilledHabits() {
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 1L);
        FulfilledHabit fulfilledHabit = new FulfilledHabit(1L, 1L, LocalDate.now());

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "user", "user@example.com", "password", false, true)));
        when(fulfilledHabitRepository.findAll()).thenReturn(List.of(fulfilledHabit));

        habitService.delete(1L);

        verify(habitRepository).deleteById(1L);
        verify(fulfilledHabitRepository).deleteById(1L);
    }

    @Test
    @DisplayName("[markHabitAsFulfilled] Должен создать новую выполненную привчку")
    public void markHabitAsFulfilledShouldCreateFulfilledHabit() {
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 1L);

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "user", "user@example.com", "password", false, true)));

        SimpleLocalDateDto localDateDto = new SimpleLocalDateDto();
        localDateDto.setDate(LocalDate.now());
        habitService.markHabitAsFulfilled(1L, localDateDto);

        verify(fulfilledHabitRepository).create(any(FulfilledHabit.class));
    }
}
