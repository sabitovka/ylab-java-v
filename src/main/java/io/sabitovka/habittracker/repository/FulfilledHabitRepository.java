package io.sabitovka.habittracker.repository;

import io.sabitovka.habittracker.model.FulfilledHabit;

/**
 * Репозиторий для управления сущностями {@link FulfilledHabit}. Расширяет базовый интерфейс {@link BaseRepository} с операциями CRUD.
 * Представляет выполненные привычки.
 */
public interface FulfilledHabitRepository extends BaseRepository<Long, FulfilledHabit>{
}
