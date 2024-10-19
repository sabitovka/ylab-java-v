package io.sabitovka.service;

import io.sabitovka.dto.HabitInfoDto;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;

import java.time.LocalDate;
import java.util.List;

/**
 * Интерфейс для управления привычками пользователей
 */
public interface HabitService {
    /**
     * Создает новую привычку по переданной информации
     * @param habitInfoDto Информация о привычке для добавления
     * @return Модель новой привычки
     */
    Habit createHabit(HabitInfoDto habitInfoDto);

    /**
     * Обновляет привычку по переданной информации
     * @param habitInfoDto Информация о привычке
     * @param currentUserId ИД текущего пользователя
     */
    void updateHabit(HabitInfoDto habitInfoDto, Long currentUserId);

    /**
     * Получить список всех привычек по фильтрам. Например, за определенный период
     * @param currentUser Пользователь
     * @param startDate Начальная дата создания привычек
     * @param endDate Дата окончания привычек
     * @param isActive Активны ли привычки
     * @return Список привычек
     */
    List<HabitInfoDto> getHabitsByFilters(User currentUser, LocalDate startDate, LocalDate endDate, Boolean isActive);

    /**
     * Получить все привычки определенного пользователя
     * @param currentUser Пользователь
     * @return Список привычек
     */
    List<HabitInfoDto> getAllByOwner(User currentUser);

    /**
     * Отключает привычку
     * @param habit Привычка
     */
    void disableHabit(Habit habit);

    /**
     * Получает информацию о привычке по ID
     * @param id Id привычки
     * @param userId Id пользователя, который запросил эту привычку
     * @return Информация о пользователе
     */
    HabitInfoDto getHabitById(Long id, Long userId);

    /**
     * Позволяет удалить привычку по ID
     * @param habitId Id привычки
     * @param currentUserId Пользователь, который инициировал удаление
     */
    void delete(Long habitId, Long currentUserId);

    /**
     * Отметить выполнение привычки.
     * @param habitId Id привычки
     * @param date Дата выполнения привычки
     * @param currentUserId Пользователь, который инициировал отметку привычки
     */
    void markHabitAsFulfilled(Long habitId, LocalDate date, Long currentUserId);

    /**
     * Преобразует модель привычки в информацию о привычке
     * @param habit Привычка
     * @return Информация о привычке
     */
    HabitInfoDto mapHabitToHabitInfoDto(Habit habit);
}
