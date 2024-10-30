package io.sabitovka.service;

import io.sabitovka.dto.user.*;
import io.sabitovka.model.User;
import io.sabitovka.util.logging.annotation.Audit;
import io.sabitovka.util.logging.annotation.IgnoreAudit;

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
    @Audit(action = "Выполнено создание нового пользователя")
    UserInfoDto createUser(CreateUserDto createUserDto);

    /**
     * Обновляет пользователя по переданным данным
     *
     * @param userId
     * @param updateUserDto Информация о пользователе
     */
    @Audit(action = "Выполнено обновление пользователя")
    void updateUser(Long userId, UpdateUserDto updateUserDto);

    /**
     * Меняет пароль пользователя по переданным данным. Для корректной смены, необходимо ввести старый пароль пользователя
     *
     * @param userInfoDto Информация о пользователе с новым паролем
     * @param oldPassword Старый пароль пользователя
     * @param userId
     */
    @Audit(action = "Выполнено обновление пароля пользователя")
    void changePassword(Long userId, @IgnoreAudit ChangePasswordDto changePasswordDto);

    /**
     * Удаляет профиль пользователя. Для подтверждения нужно указать действующий пароль пользователя
     *
     * @param deleteProfileDto
     */
    @Audit(action = "Выполнено удаление профиля пользователя")
    void deleteProfile(Long deleteProfileDto);

    /**
     * Блокирует пользователя по переданному ID
     *
     * @param userId ID пользователя
     */
    @Audit(action = "Выполнена блокировка пользователя")
    void blockUser(Long userId);

    /**
     * Находит пользователя по ID. Возвращает информацию о пользователе
     * @param userId Id пользователя для поиска
     * @return Информацию о пользователе
     */
    @Audit(action = "Выполнен поиск пользователя по ID")
    UserInfoDto findById(Long userId);

    /**
     * Возвращает список заблокированных пользователей
     *
     * @return Список неактивных пользователей
     */
    @Audit(action = "Получены заблокированные пользователи")
    List<UserInfoDto> getBlockedUsers();

    /**
     * Возвращает Список активных пользователей
     * @return Список активных пользователей
     */
    @Audit(action = "Получены активные пользователи")
    List<UserInfoDto> getActiveUsers();
}
