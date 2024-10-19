package io.sabitovka.repository.impl;

import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.User;
import io.sabitovka.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализация интерфейса {@link UserRepository} для управления пользователями в памяти.
 * Использует {@link HashMap} для хранения пользователей и индексацию по email для уникальности.
 * В рамках данного репозитория отсутствует интеграция с базой данных.
 */
public class UserRepositoryImpl implements UserRepository {
    /**
     * Счетчик для генерации уникальных идентификаторов пользователей.
     */
    private final AtomicLong usersCounter = new AtomicLong(0);

    /**
     * Хранилище пользователей, где ключом является их уникальный идентификатор.
     */
    private final HashMap<Long, User> users = new HashMap<>();

    /**
     * Индекс для обеспечения уникальности email.
     * Ключ - email, значение - идентификатор пользователя.
     */
    private final HashMap<String, Long> emailIndex = new HashMap<>();

    /**
     * Проверяет, существует ли пользователь с указанным идентификатором.
     *
     * @param id идентификатор пользователя.
     * @return {@code true}, если пользователь существует, иначе {@code false}.
     */
    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

    /**
     * Проверяет уникальность email и выбрасывает исключение, если email уже используется.
     *
     * @param email email для проверки.
     * @throws IllegalArgumentException если email уже существует в системе.
     */
    private void checkEmailIndexConstraint(String email) {
        if (emailIndex.containsKey(email)) {
            throw new IllegalArgumentException("Нарушение индекса Email");
        }
    }

    /**
     * Создает нового пользователя в системе.
     *
     * @param user данные создаваемого пользователя.
     * @return новый пользователь с присвоенным идентификатором.
     * @throws IllegalArgumentException если пользователь равен null или email уже существует.
     * @throws EntityAlreadyExistsException если пользователь с таким ID уже существует.
     */
    @Override
    public User create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }

        if (existsById(user.getId())) {
            throw new EntityAlreadyExistsException(user.getId());
        }

        checkEmailIndexConstraint(user.getEmail());

        long userId = usersCounter.incrementAndGet();

        User newUser = user.toBuilder().build();
        newUser.setId(userId);

        users.put(userId, newUser);
        emailIndex.put(newUser.getEmail(), userId);

        return newUser.toBuilder().build();
    }

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя.
     * @return {@link Optional}, содержащий пользователя, если он найден, или пустой {@link Optional}.
     */
    @Override
    public Optional<User> findById(Long id) {
        User user = users.get(id);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(user.toBuilder().build());
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список всех пользователей.
     */
    @Override
    public List<User> findAll() {
        return users.values().stream()
                .map(user -> user.toBuilder().build())
                .toList();
    }

    /**
     * Обновляет информацию о пользователе.
     *
     * @param user пользователь с обновленными данными.
     * @return {@code true}, если обновление выполнено успешно.
     * @throws IllegalArgumentException если пользователь или его идентификатор равен null.
     * @throws EntityNotFoundException если пользователь не найден.
     */
    @Override
    public boolean update(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User is null or user.id is null");
        }

        User existedUser = users.get(user.getId());
        if (existedUser == null) {
            throw new EntityNotFoundException(user.getId());
        }

        if (!existedUser.getEmail().equalsIgnoreCase(user.getEmail())) {
            checkEmailIndexConstraint(user.getEmail());
            emailIndex.remove(existedUser.getEmail());
        }

        emailIndex.put(user.getEmail(), existedUser.getId());

        existedUser.setName(user.getName());
        existedUser.setEmail(user.getEmail());
        existedUser.setPassword(user.getPassword());
        existedUser.setActive(user.isActive());
        return true;
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя.
     * @return {@code true}, если пользователь был успешно удален.
     */
    @Override
    public boolean deleteById(Long id) {
        return users.remove(id) != null;
    }

    /**
     * Ищет пользователя по его email.
     *
     * @param email email для поиска.
     * @return {@link Optional}, содержащий пользователя, если он найден, или пустой {@link Optional}.
     */
    @Override
    public Optional<User> findUserByEmail(String email) {
        Long userId = emailIndex.get(email);
        return findById(userId);
    }
}

