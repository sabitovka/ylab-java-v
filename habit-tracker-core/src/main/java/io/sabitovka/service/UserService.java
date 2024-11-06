package io.sabitovka.service;

import io.sabitovka.annotation.IgnoreAudit;
import io.sabitovka.dto.user.ChangePasswordDto;
import io.sabitovka.dto.user.CreateUserDto;
import io.sabitovka.dto.user.UpdateUserDto;
import io.sabitovka.dto.user.UserInfoDto;
import io.sabitovka.model.User;

import java.util.List;

/**
 * Интерфейс сервиса для управления пользователями {@link User}
 */
public interface UserService {
    /**
     * Создает нового пользователя по переданным данным от пользователя
     * @param createUserDto Данные, по которым будет создан новый пользователь
     * @return Новый пользователь
     */
    UserInfoDto createUser(CreateUserDto createUserDto);

    /**
     * Обновляет пользователя по переданным данным
     *
     * @param userId - ID пользователя, которого нужно обновить
     * @param updateUserDto Информация о пользователе
     */
    void updateUser(Long userId, UpdateUserDto updateUserDto);

    /**
     * Меняет пароль пользователя по переданным данным. Для корректной смены, необходимо ввести старый пароль пользователя
     *
     * @param changePasswordDto Информация о пользователе для смены пароля
     * @param userId - ID пользователя, чей пароль нужно поменять
     */
    void changePassword(Long userId, @IgnoreAudit ChangePasswordDto changePasswordDto);

    /**
     * Удаляет профиль пользователя. Для подтверждения нужно указать действующий пароль пользователя
     *
     * @param deleteProfileDto - Данные для удаления профиля
     */
    void deleteProfile(Long deleteProfileDto);

    /**
     * Блокирует пользователя по переданному ID
     *
     * @param userId ID пользователя
     */
    void blockUser(Long userId);

    /**
     * Находит пользователя по ID. Возвращает информацию о пользователе
     * @param userId Id пользователя для поиска
     * @return Информацию о пользователе
     */
    UserInfoDto findById(Long userId);

    /**
     * Возвращает список заблокированных пользователей
     *
     * @return Список неактивных пользователей
     */
    List<UserInfoDto> getBlockedUsers();

    /**
     * Возвращает Список активных пользователей
     * @return Список активных пользователей
     */
    List<UserInfoDto> getActiveUsers();
}
