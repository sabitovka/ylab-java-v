package io.sabitovka.service;

import io.sabitovka.dto.habit.HabitFilterDto;
import io.sabitovka.dto.habit.HabitInfoDto;
import io.sabitovka.dto.habit.SimpleLocalDateDto;
import io.sabitovka.annotation.Audit;

import java.util.List;

/**
 * Интерфейс для управления привычками пользователей
 */
public interface HabitService {
    /**
     * Создает новую привычку по переданной информации
     *
     * @param habitInfoDto Информация о привычке для добавления
     * @return Модель новой привычки
     */
    @Audit(action = "Создана новая привычка")
    HabitInfoDto createHabit(HabitInfoDto habitInfoDto);

    /**
     * Обновляет привычку по переданной информации
     *
     * @param habitInfoDto Информация о привычке
     * @param habitId - ID привычки
     */
    @Audit(action = "Привычка обновлена")
    void updateHabit(Long habitId, HabitInfoDto habitInfoDto);

    /**
     * Получить список всех привычек по фильтрам. Например, за определенный период
     *
     * @param filterDto@return Список привычек
     */
    @Audit(action = "Получение привычек по фильтрам")
    List<HabitInfoDto> getHabitsByFilters(HabitFilterDto filterDto);

    /**
     * Получить все привычки определенного пользователя
     *
     * @param userId Пользователь
     * @return Список привычек
     */
    @Audit(action = "Получение привычек пользователя")
    List<HabitInfoDto> getAllByOwner(Long userId);

    /**
     * Отключает привычку
     *
     * @param habitId Привычка
     */
    @Audit(action = "Отключение привычки")
    void disableHabit(Long habitId);

    /**
     * Получает информацию о привычке по ID
     *
     * @param id Id привычки
     * @return Информация о пользователе
     */
    @Audit(action = "Получение привычки по Id")
    HabitInfoDto getHabitById(Long id);

    /**
     * Позволяет удалить привычку по ID
     *
     * @param habitId Id привычки
     */
    @Audit(action = "Удаление привычки")
    void delete(Long habitId);

    /**
     * Отметить выполнение привычки.
     *
     * @param habitId      Id привычки
     * @param localDateDto Пользователь, который инициировал отметку привычки
     */
    @Audit(action = "Выполнение привычки")
    void markHabitAsFulfilled(Long habitId, SimpleLocalDateDto localDateDto);
}
