package io.sabitovka.repository;

import io.sabitovka.model.Habit;

import java.time.LocalDate;
import java.util.List;

/**
 * Репозиторий для управления сущностями {@link Habit}. Расширяет базовый интерфейс {@link BaseRepository} с операциями CRUD.
 * Предоставляет дополнительные методы для фильтрации и поиска привычек.
 */
public interface HabitRepository extends BaseRepository<Long, Habit> {
    /**
     * Возвращает список всех привычек, принадлежащих указанному пользователю.
     *
     * @param ownerId Владелец привычек.
     * @return Список всех привычек, принадлежащих пользователю.
     */
    List<Habit> findAllByUserId(Long ownerId);

    /**
     * Фильтрует привычки пользователя по времени их создания и статусу активности.
     *
     * @param ownerId ID Владельца привычек.
     * @param startDate Дата начала периода фильтрации.
     * @param endDate Дата окончания периода фильтрации.
     * @param isActive Флаг активности привычек.
     * @return Список привычек, соответствующих фильтру.
     */
    List<Habit> filterByUserAndTimeAndStatus(Long ownerId, LocalDate startDate, LocalDate endDate, Boolean isActive);

    /**
     * Удаляет привычку и всю историю ее выполнения
     *
     * @param habit Привычка
     */
    void deleteWithHistoryByHabit(Habit habit);
}
