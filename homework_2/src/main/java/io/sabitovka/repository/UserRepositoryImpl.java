package io.sabitovka.repository;

import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.exception.EntityConstraintException;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.persistence.JdbcTemplate;
import io.sabitovka.model.User;
import io.sabitovka.persistence.PersistenceRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class UserRepositoryImpl extends PersistenceRepository<Long, User> implements UserRepository {
    private final AtomicLong usersCounter = new AtomicLong(0);
    private final HashMap<Long, User> users = new HashMap<>();
    private final HashMap<String, Long> emailIndex = new HashMap<>();

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, User.class);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "select exists(select 1 from users where id = ?";
        return users.containsKey(id);
    }

    private void checkEmailIndexConstraint(String email) {
        if (emailIndex.containsKey(email)) {
            throw new EntityConstraintException("Нарушение индекса Email");
        }
    }

    @Override
    public User create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }

        if (existsById(user.getId())) {
            throw new EntityAlreadyExistsException("Пользователь уже существует в системе");
        }

        checkEmailIndexConstraint(user.getEmail());

        long userId = usersCounter.incrementAndGet();

        User newUser = new User(user);
        newUser.setId(userId);

        users.put(userId, newUser);
        emailIndex.put(newUser.getEmail(), userId);

        return new User(newUser);
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().map(User::new).collect(Collectors.toList());
    }

    @Override
    public boolean update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }

        User existedUser = users.get(user.getId());
        if (existedUser == null) {
            throw new EntityNotFoundException("Пользователь не найден в системе");
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

    @Override
    public boolean deleteById(Long id) {
        return users.remove(id) != null;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        Long userId = emailIndex.get(email);
        return findById(userId);
    }
}
