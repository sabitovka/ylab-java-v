package io.sabitovka.service;

import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.model.User;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.util.PasswordHasher;

import java.util.Optional;

public class AuthorizationService {

    private final UserRepository userRepository;
    private final UserService userService;
    public AuthorizationService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public User register(String name, String email, String password) {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new EntityAlreadyExistsException("Данный email уже занят");
        }

        String hashedPassword = PasswordHasher.hash(password);
        User user = new User(name, email, hashedPassword);
        return userRepository.create(user);
    }

    public boolean login(String email, String password) {
        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isEmpty()) {
            return false;
        }

        if (PasswordHasher.verify(password, user.get().getPassword())) {
            userService.setCurrentUser(user.get());
            return true;
        }

        return false;
    }
}
