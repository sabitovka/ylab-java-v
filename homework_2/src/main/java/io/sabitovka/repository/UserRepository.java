package io.sabitovka.repository;

import io.sabitovka.model.User;

import java.util.Optional;

/**
 * Репозиторий для управления сущностями {@link User}. Расширяет базовый интерфейс {@link BaseRepository} с операциями CRUD.
 * Предоставляет дополнительные методы для работы с пользователями.
 */
public interface UserRepository extends BaseRepository<Long, User> {
    /**
     * Ищет пользователя по его email.
     *
     * @param email Email пользователя, по которому выполняется поиск.
     * @return {@link Optional}, содержащий пользователя, если он найден, или пустой {@link Optional}, если нет.
     */
    Optional<User> findUserByEmail(String email);
}
