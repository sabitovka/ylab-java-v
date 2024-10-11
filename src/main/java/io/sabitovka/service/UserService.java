package io.sabitovka.service;

import io.sabitovka.common.Constants;
import io.sabitovka.model.User;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.util.PasswordHasher;

import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;
    private User currentUser;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void changeName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Новое имя пользователя не может быть пустым");
        }

        if (currentUser == null) {
            throw new IllegalStateException("Пользователь не авторизован");
        }

        currentUser.setName(newName.trim());
        userRepository.update(currentUser);
    }

    public void changeEmail(String newEmail) {
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Новый email не может быть пустым");
        }

        if (currentUser == null) {
            throw new IllegalStateException("Пользователь не авторизован");
        }

        if (!newEmail.matches(Constants.EMAIL_REGEX)) {
            throw new IllegalArgumentException("Некорректный формат email");
        }

        Optional<User> userWithEmail = userRepository.findUserByEmail(newEmail.trim());
        if (userWithEmail.isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        User updatedUser = new User(currentUser.getId(), currentUser.getName(), newEmail.trim(), currentUser.getPassword());
        userRepository.update(updatedUser);
        currentUser.setEmail(newEmail.trim());
    }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Новый пароль не может быть пустым");
        }

        if (currentUser == null) {
            throw new IllegalStateException("Пользователь не авторизован");
        }

        if (!newPassword.matches(Constants.PASSWORD_REGEX)) {
            throw new IllegalArgumentException("Новый пароль не соответствует требованиям");
        }

        String hashedPassword = PasswordHasher.hash(newPassword);
        User updatedUser = new User(currentUser.getId(), currentUser.getName(), currentUser.getEmail(), hashedPassword);
        userRepository.update(updatedUser);
        currentUser.setPassword(hashedPassword);
    }

    public void deleteProfile(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        if (currentUser == null) {
            throw new IllegalStateException("Пользователь не авторизован");
        }

        if (!password.matches(Constants.PASSWORD_REGEX)) {
            throw new IllegalArgumentException("Пароль не соответствует требованиям");
        }

        String hashedPassword = PasswordHasher.hash(password);
        if (currentUser.getPassword().equals(hashedPassword)) {
            userRepository.deleteById(currentUser.getId());
        }
    }
}
