package io.sabitovka.habittracker.service;

import io.sabitovka.habittracker.auth.AuthInMemoryContext;
import io.sabitovka.habittracker.auth.entity.UserDetails;
import io.sabitovka.habittracker.dto.habit.HabitFilterDto;
import io.sabitovka.habittracker.dto.habit.HabitInfoDto;
import io.sabitovka.habittracker.dto.habit.SimpleLocalDateDto;
import io.sabitovka.habittracker.enums.HabitFrequency;
import io.sabitovka.habittracker.exception.ApplicationException;
import io.sabitovka.habittracker.model.FulfilledHabit;
import io.sabitovka.habittracker.model.Habit;
import io.sabitovka.habittracker.model.User;
import io.sabitovka.habittracker.repository.FulfilledHabitRepository;
import io.sabitovka.habittracker.repository.HabitRepository;
import io.sabitovka.habittracker.repository.UserRepository;
import io.sabitovka.habittracker.service.impl.HabitServiceImpl;
import io.sabitovka.habittracker.util.mapper.HabitMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private AuthInMemoryContext authInMemoryContext;
    private HabitMapper habitMapper = Mappers.getMapper(HabitMapper.class);
    @InjectMocks
    private HabitServiceImpl habitService;

    private final UserDetails simpleUserDetails = new UserDetails(2L, "user", false);

    @BeforeEach
    void setUp() {
        habitService = new HabitServiceImpl(habitRepository, userRepository, fulfilledHabitRepository, habitMapper);
    }

    @Test
    @DisplayName("[createHabit] Должен создать привычку")
    public void createHabitWhenValidShouldCreateHabit() {
        HabitInfoDto habitInfoDto = new HabitInfoDto(null, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 2L);
        when(userRepository.existsById(2L)).thenReturn(true);

        Habit newHabit = new Habit(null, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 2L);
        when(habitRepository.create(any(Habit.class))).thenReturn(newHabit);

        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(authInMemoryContext.getAuthentication()).thenReturn(simpleUserDetails);

            HabitInfoDto createdHabit = habitService.createHabit(habitInfoDto);

            assertThat(createdHabit.getName()).isEqualTo("habitName");
            verify(habitRepository).create(any(Habit.class));
        }

    }

    @Test
    @DisplayName("[createHabit] Когда пользователь не владелец, должен выбросить исключение")
    public void createHabitWhenInvalidOwnerShouldThrowException() {
        HabitInfoDto habitInfoDto = new HabitInfoDto(null, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 999L);

        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(authInMemoryContext.getAuthentication()).thenReturn(simpleUserDetails);

            assertThatThrownBy(() -> habitService.createHabit(habitInfoDto))
                    .isInstanceOf(ApplicationException.class);
        }

    }

    @Test
    @DisplayName("[getHabitsByFilters] Должен вернуть отфильтрованные привычки")
    public void getHabitsByFiltersShouldReturnFilteredHabits() {
        Habit habit = new Habit(2L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 2L);
        when(habitRepository.filterByUserAndTimeAndStatus(2L, null, null, true)).thenReturn(List.of(habit));

        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(authInMemoryContext.getAuthentication()).thenReturn(simpleUserDetails);

            HabitFilterDto filterDto = new HabitFilterDto();
            filterDto.setUserId(2L);
            filterDto.setActive(true);
            List<HabitInfoDto> habitDtos = habitService.getHabitsByFilters(filterDto);

            assertThat(habitDtos).hasSize(1);
            assertThat(habitDtos.get(0).getName()).isEqualTo("habitName");
        }
    }

    @Test
    @DisplayName("[getAllByOwner] Должен вернуть привычки пользователя")
    public void getAllByOwnerShouldReturnHabitsOfUser() {
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 2L);
        when(habitRepository.findAllByUserId(2L)).thenReturn(List.of(habit));

        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(authInMemoryContext.getAuthentication()).thenReturn(simpleUserDetails);

            List<HabitInfoDto> habitDtos = habitService.getAllByOwner(2L);

            assertThat(habitDtos).hasSize(1);
            assertThat(habitDtos.get(0).getName()).isEqualTo("habitName");
        }
    }

    @Test
    @DisplayName("[disableHabit] Должен отключить привычку")
    public void disableHabitShouldSetHabitInactive() {
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 2L);
        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()).map(user -> {
            user.setId(2L);
            return user;
        }));

        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(authInMemoryContext.getAuthentication()).thenReturn(simpleUserDetails);

            habitService.disableHabit(1L);

            assertThat(habit.isActive()).isFalse();
            verify(habitRepository).update(habit);
        }
    }

    @Test
    @DisplayName("[updateHabit] Должен обновить привычку")
    public void updateHabitWhenValidShouldUpdateSuccessfully() {
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 2L);
        HabitInfoDto updatedHabit = new HabitInfoDto(1L, "newName", "newDescription", HabitFrequency.WEEKLY, LocalDate.now(), true, 2L);

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "user", "user@example.com", "password", false, true)));


        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(authInMemoryContext.getAuthentication()).thenReturn(simpleUserDetails);

            habitService.updateHabit(1L, updatedHabit);

            assertThat(habit.getName()).isEqualTo("newName");
            assertThat(habit.getDescription()).isEqualTo("newDescription");
            assertThat(habit.getFrequency()).isEqualTo(HabitFrequency.WEEKLY);
            verify(habitRepository).update(habit);
        }
    }

    @Test
    @DisplayName("[delete] Должен удалить привычку и историю выполнений")
    public void deleteShouldDeleteHabitAndFulfilledHabits() {
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 2L);
        FulfilledHabit fulfilledHabit = new FulfilledHabit(1L, 1L, LocalDate.now());

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "user", "user@example.com", "password", false, true)));

        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(authInMemoryContext.getAuthentication()).thenReturn(simpleUserDetails);

            habitService.delete(1L);

            verify(habitRepository).deleteById(1L);
            verify(fulfilledHabitRepository).deleteByHabitId(1L);
        }
    }

    @Test
    @DisplayName("[markHabitAsFulfilled] Должен создать новую выполненную привчку")
    public void markHabitAsFulfilledShouldCreateFulfilledHabit() {
        Habit habit = new Habit(1L, "habitName", "description", HabitFrequency.DAILY, LocalDate.now(), true, 2L);

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "user", "user@example.com", "password", false, true)));

        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(authInMemoryContext.getAuthentication()).thenReturn(simpleUserDetails);

            SimpleLocalDateDto localDateDto = new SimpleLocalDateDto();
            localDateDto.setDate(LocalDate.now());
            habitService.markHabitAsFulfilled(1L, localDateDto);

            verify(fulfilledHabitRepository).create(any(FulfilledHabit.class));
        }
    }
}
