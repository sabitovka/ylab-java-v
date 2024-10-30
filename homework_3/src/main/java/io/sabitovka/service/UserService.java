package io.sabitovka.service;

import io.sabitovka.dto.user.CreateUserDto;
import io.sabitovka.dto.user.UserInfoDto;
import io.sabitovka.model.User;
import io.sabitovka.util.logging.annotation.Audit;

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
     * @param userInfoDto Информация о пользователе
     */
    void updateUser(UserInfoDto userInfoDto);

    /**
     * Меняет пароль пользователя по переданным данным. Для корректной смены, необходимо ввести старый пароль пользователя
     * @param userInfoDto Информация о пользователе с новым паролем
     * @param oldPassword Старый пароль пользователя
     */
    void changePassword(UserInfoDto userInfoDto, String oldPassword);

    /**
     * Удаляет профиль пользователя. Для подтверждения нужно указать действующий пароль пользователя
     * @param profileId Id профиля пользователя
     * @param password Пароль пользователя
     */
    void deleteProfile(Long profileId, String password);

    /**
     * Блокирует пользователя по переданному ID
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

    /**
     * Преобразует информацию о пользователе {@link UserInfoDto} в модель {@link User}
     * @param userInfoDto Информация о пользователе
     * @return Модель пользователя
     */
    User mapUserInfoToUser(UserInfoDto userInfoDto);

    /**
     * Преобразует модель пользователя {@link User} в информацию о пользователе {@link UserInfoDto}
     * @param user Модель пользователя
     * @return Информация о пользователе
     */
    UserInfoDto mapUserToUserInfo(User user);
}
