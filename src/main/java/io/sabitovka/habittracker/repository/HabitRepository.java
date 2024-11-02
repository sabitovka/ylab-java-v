package io.sabitovka.habittracker.repository;

import io.sabitovka.habittracker.model.Habit;
import io.sabitovka.habittracker.model.User;

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
     * @param owner Владелец привычек.
     * @return Список всех привычек, принадлежащих пользователю.
     */
    List<Habit> findAllByUser(User owner);

    /**
     * Фильтрует привычки пользователя по времени их создания и статусу активности.
     *
     * @param owner Владелец привычек.
     * @param startDate Дата начала периода фильтрации.
     * @param endDate Дата окончания периода фильтрации.
     * @param isActive Флаг активности привычек.
     * @return Список привычек, соответствующих фильтру.
     */
    List<Habit> filterByUserAndTimeAndStatus(User owner, LocalDate startDate, LocalDate endDate, Boolean isActive);
}
