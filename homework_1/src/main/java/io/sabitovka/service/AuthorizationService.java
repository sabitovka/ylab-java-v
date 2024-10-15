package io.sabitovka.service;

import io.sabitovka.model.User;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.util.PasswordHasher;

import java.util.Optional;

public class AuthorizationService {

    private final UserRepository userRepository;
    private Long currentUserId;
    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        if (currentUserId == null) {
            throw new IllegalStateException("Ошибка авторизации. Выполните вход");
        }
        return userRepository.findById(currentUserId).orElseThrow(() -> new IllegalStateException("Ошибка авторизации. Повторите вход"));
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
