package io.sabitovka.habittracker.service.impl;

import io.sabitovka.habittracker.model.User;
import io.sabitovka.habittracker.repository.UserRepository;
import io.sabitovka.habittracker.service.AuthorizationService;
import io.sabitovka.habittracker.util.PasswordHasher;

import java.util.Optional;

/**
 *
 */
public class AuthorizationServiceImpl implements AuthorizationService {
    private final UserRepository userRepository;
    private Long currentUserId;
    public AuthorizationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        if (currentUserId == null) {
            throw new IllegalStateException("Ошибка авторизации. Выполните вход");
        }
        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("Ошибка авторизации. Повторите вход"));
    }

    public boolean isAdmin() {
        return getCurrentUser().isAdmin();
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void login(String email, String password) {
        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isEmpty() || !PasswordHasher.verify(password, user.get().getPassword())) {
            return;
        }

        if (!user.get().isActive()) {
            throw new IllegalStateException("Ваша учетная запись была заблокирована");
        }

        this.currentUserId = user.get().getId();
    }

    public void logout() {
        this.currentUserId = null;
    }

    public boolean isLoggedIn() {
        return currentUserId != null;
    }
}
