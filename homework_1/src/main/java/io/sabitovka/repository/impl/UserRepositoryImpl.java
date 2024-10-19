package io.sabitovka.repository.impl;

import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.User;
import io.sabitovka.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class UserRepositoryImpl implements UserRepository {
    private final AtomicLong usersCounter = new AtomicLong(0);
    private final HashMap<Long, User> users = new HashMap<>();
    private final HashMap<String, Long> emailIndex = new HashMap<>();

    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

    private void checkEmailIndexConstraint(String email) {
        if (emailIndex.containsKey(email)) {
            throw new IllegalArgumentException("Нарушение индекса Email");
        }
    }

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

    @Override
    public Optional<User> findById(Long id) {
        User user = users.get(id);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(user.toBuilder().build());
    }

    @Override
    public List<User> findAll() {
        return users.values().stream()
                .map(user -> user.toBuilder().build())
                .toList();
    }

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
