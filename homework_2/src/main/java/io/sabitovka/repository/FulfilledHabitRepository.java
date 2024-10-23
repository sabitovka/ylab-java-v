package io.sabitovka.repository;

import io.sabitovka.model.FulfilledHabit;

/**
 * Репозиторий для управления сущностями {@link FulfilledHabit}. Расширяет базовый интерфейс {@link BaseRepository} с операциями CRUD.
 * Представляет выполненные привычки.
 */
public interface FulfilledHabitRepository extends BaseRepository<Long, FulfilledHabit>{
}
