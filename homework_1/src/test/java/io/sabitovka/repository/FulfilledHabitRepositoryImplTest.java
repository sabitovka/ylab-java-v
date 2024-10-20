package io.sabitovka.repository;

import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.repository.impl.FulfilledHabitRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Тест репозитория FulfilledHabitRepositoryImpl")
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
    @DisplayName("[create] Должен успешно создать выполненную привычку")
    public void createShouldCreateFulfilledHabitSuccessfully() {
        FulfilledHabit createdHabit = fulfilledHabitRepository.create(fulfilledHabit1);

        assertThat(fulfilledHabitRepository.existsById(createdHabit.getId())).isTrue();
        assertThat(createdHabit.getHabitId()).isEqualTo(fulfilledHabit1.getHabitId());
    }

    @Test
    @DisplayName("[create] Когда выполненная привычка = null, должен выбросить исключение")
    public void createWhenFulfilledHabitIsNullShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> fulfilledHabitRepository.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("FulfilledHabit is null");
    }

    @Test
    @DisplayName("[create] Когда выполненная привычка уже есть в системе, должен выбросить исключение")
    public void createWhenFulfilledHabitAlreadyExistsShouldThrowEntityAlreadyExistsException() {
        fulfilledHabitRepository.create(fulfilledHabit1);

        assertThatThrownBy(() -> fulfilledHabitRepository.create(fulfilledHabit1))
                .isInstanceOf(EntityAlreadyExistsException.class);
    }

    @Test
    @DisplayName("[findById] Когда выполненная привычка уже есть, должен вернуть ее")
    public void findByIdWhenFulfilledHabitExistsShouldReturnFulfilledHabit() {
        FulfilledHabit createdHabit = fulfilledHabitRepository.create(fulfilledHabit1);

        Optional<FulfilledHabit> foundHabit = fulfilledHabitRepository.findById(createdHabit.getId());

        assertThat(foundHabit.isPresent()).isTrue();
        assertThat(foundHabit.get().getHabitId()).isEqualTo(fulfilledHabit1.getHabitId());
    }

    @Test
    @DisplayName("[findById] Когда выполненной привычки нет, должен вернуть пустой Optional")
    public void findByIdWhenFulfilledHabitDoesNotExistShouldReturnEmptyOptional() {
        Optional<FulfilledHabit> foundHabit = fulfilledHabitRepository.findById(999L);

        assertThat(foundHabit.isPresent()).isFalse();
    }

    @Test
    @DisplayName("[findAll] Должен успешно вернуть выполненные привычки")
    public void findAllShouldReturnAllFulfilledHabits() {
        fulfilledHabitRepository.create(fulfilledHabit1);
        fulfilledHabitRepository.create(fulfilledHabit2);

        List<FulfilledHabit> habits = fulfilledHabitRepository.findAll();

        assertThat(habits).hasSize(2);
    }

    @Test
    @DisplayName("[update] Не поддерживается. Ожидается исключение")
    public void updateShouldThrowUnsupportedOperationException() {
        assertThatThrownBy(() -> fulfilledHabitRepository.update(fulfilledHabit1))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Обновление привычки не поддерживается");
    }

    @Test
    @DisplayName("[deleteById] Когда выполненная привычка имеется, должен удалить успешно")
    public void deleteByIdWhenFulfilledHabitExistsShouldDeleteSuccessfully() {
        FulfilledHabit createdHabit = fulfilledHabitRepository.create(fulfilledHabit1);

        boolean deleted = fulfilledHabitRepository.deleteById(createdHabit.getId());

        assertThat(deleted).isTrue();
        assertThat(fulfilledHabitRepository.findById(createdHabit.getId()).isPresent()).isFalse();
    }

    @Test
    @DisplayName("[deleteById] Когда выполненной привычки нет, должен вернуть false")
    public void deleteByIdWhenFulfilledHabitDoesNotExistShouldReturnFalse() {
        boolean deleted = fulfilledHabitRepository.deleteById(999L);

        assertThat(deleted).isFalse();
    }

}
