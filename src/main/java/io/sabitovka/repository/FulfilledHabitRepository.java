package io.sabitovka.repository;

import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.model.Habit;

import java.util.List;

/**
 * Репозиторий для управления сущностями {@link FulfilledHabit}. Расширяет базовый интерфейс {@link BaseRepository} с операциями CRUD.
 * Представляет выполненные привычки.
 */
public interface FulfilledHabitRepository extends BaseRepository<Long, FulfilledHabit>{
    /**
     * Ищет все выполненные привычки
     * @param habit Привычка
     * @return Историю выполнения привычки
     */
    List<FulfilledHabit> findAllByHabit(Habit habit);

    /**
     * Удаляет историю привычки
     * @param habitId ID привычки
     */
    void deleteByHabitId(Long habitId);
}