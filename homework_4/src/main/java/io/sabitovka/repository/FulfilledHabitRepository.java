package io.sabitovka.repository;

import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.model.Habit;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для управления сущностями {@link FulfilledHabit}. Расширяет базовый интерфейс {@link BaseRepository} с операциями CRUD.
 * Представляет выполненные привычки.
 */
public interface FulfilledHabitRepository extends BaseRepository<Long, FulfilledHabit>{
    List<FulfilledHabit> findAllByHabit(Habit habit);
}
