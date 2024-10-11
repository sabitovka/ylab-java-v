package io.sabitovka.service;

import io.sabitovka.common.Constants;
import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.model.User;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.util.PasswordHasher;

import java.util.Optional;

public class AuthorizationService {

    private final UserRepository userRepository;
    private User currentUser;
    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            throw new IllegalStateException("Нет авторизованного пользователя");
        }
        return currentUser;
    }

    private void validateRegistrationInput(String name, String email, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (email == null || !email.matches(Constants.EMAIL_REGEX)) {
            throw new IllegalArgumentException("Неправильный формат email");
        }
        if (password == null || !password.matches(Constants.PASSWORD_REGEX)) {
            throw new IllegalArgumentException("Пароль не соответствует требованиям");
        }
    }

    public User register(String name, String email, String password) {
        validateRegistrationInput(name, email, password);

        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new EntityAlreadyExistsException("Данный email уже занят");
        }

        String hashedPassword = PasswordHasher.hash(password);
        User user = new User(name, email, hashedPassword);

        return userRepository.create(user);
    }

    public void login(String email, String password) {
        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isEmpty() || !PasswordHasher.verify(password, user.get().getPassword())) {
            return;
        }
        this.currentUser = user.get();
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
