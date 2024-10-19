package io.sabitovka.repository;

import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.repository.impl.FulfilledHabitRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FulfilledHabitRepositoryImplTest {

    private FulfilledHabitRepositoryImpl fulfilledHabitRepository;
    private FulfilledHabit fulfilledHabit1;
    private FulfilledHabit fulfilledHabit2;

    @BeforeEach
    public void setUp() {
        fulfilledHabitRepository = new FulfilledHabitRepositoryImpl();
        fulfilledHabit1 = new FulfilledHabit(1L, 1L, LocalDate.now());
        fulfilledHabit2 = new FulfilledHabit(2L, 1L, LocalDate.now().minusDays(1));
    }

    @Test
    public void create_shouldCreateFulfilledHabitSuccessfully() {
        FulfilledHabit createdHabit = fulfilledHabitRepository.create(fulfilledHabit1);
        assertThat(fulfilledHabitRepository.existsById(createdHabit.getId())).isTrue();
        assertThat(createdHabit.getHabitId()).isEqualTo(fulfilledHabit1.getHabitId());
    }

    @Test
    public void create_whenFulfilledHabitIsNull_shouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> fulfilledHabitRepository.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("FulfilledHabit is null");
    }

    @Test
    public void create_whenFulfilledHabitAlreadyExists_shouldThrowEntityAlreadyExistsException() {
        fulfilledHabitRepository.create(fulfilledHabit1);
        assertThatThrownBy(() -> fulfilledHabitRepository.create(fulfilledHabit1))
                .isInstanceOf(EntityAlreadyExistsException.class);
    }

    @Test
    public void findById_whenFulfilledHabitExists_shouldReturnFulfilledHabit() {
        FulfilledHabit createdHabit = fulfilledHabitRepository.create(fulfilledHabit1);
        Optional<FulfilledHabit> foundHabit = fulfilledHabitRepository.findById(createdHabit.getId());
        assertThat(foundHabit.isPresent()).isTrue();
        assertThat(foundHabit.get().getHabitId()).isEqualTo(fulfilledHabit1.getHabitId());
    }

    @Test
    public void findById_whenFulfilledHabitDoesNotExist_shouldReturnEmptyOptional() {
        Optional<FulfilledHabit> foundHabit = fulfilledHabitRepository.findById(999L);
        assertThat(foundHabit.isPresent()).isFalse();
    }

    @Test
    public void findAll_shouldReturnAllFulfilledHabits() {
        fulfilledHabitRepository.create(fulfilledHabit1);
        fulfilledHabitRepository.create(fulfilledHabit2);
        List<FulfilledHabit> habits = fulfilledHabitRepository.findAll();
        assertThat(habits).hasSize(2);
    }

    @Test
    public void update_shouldThrowUnsupportedOperationException() {
        assertThatThrownBy(() -> fulfilledHabitRepository.update(fulfilledHabit1))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Обновление привычки не поддерживается");
    }

    @Test
    public void deleteById_whenFulfilledHabitExists_shouldDeleteSuccessfully() {
        FulfilledHabit createdHabit = fulfilledHabitRepository.create(fulfilledHabit1);
        boolean deleted = fulfilledHabitRepository.deleteById(createdHabit.getId());
        assertThat(deleted).isTrue();
        assertThat(fulfilledHabitRepository.findById(createdHabit.getId()).isPresent()).isFalse();
    }

    @Test
    public void deleteById_whenFulfilledHabitDoesNotExist_shouldReturnFalse() {
        boolean deleted = fulfilledHabitRepository.deleteById(999L);
        assertThat(deleted).isFalse();
    }

}
