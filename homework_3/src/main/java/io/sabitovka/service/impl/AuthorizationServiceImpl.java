package io.sabitovka.service.impl;

import io.sabitovka.enums.ErrorCode;
import io.sabitovka.exception.ApplicationException;
import io.sabitovka.model.User;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.service.AuthorizationService;
import io.sabitovka.util.Jwt;
import io.sabitovka.util.PasswordHasher;

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

    public String login(String email, String password) {
        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isEmpty() || !PasswordHasher.verify(password, user.get().getPassword())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED, "Неверный логин или пароль");
        }

        if (!user.get().isActive()) {
            throw new ApplicationException(ErrorCode.FORBIDDEN, "Ваша учетная запись была заблокирована");
        }

        this.currentUserId = user.get().getId();

        return Jwt.generate(String.valueOf(currentUserId));
    }

    public void logout() {
        this.currentUserId = null;
    }

    public boolean isLoggedIn() {
        return currentUserId != null;
    }
}
