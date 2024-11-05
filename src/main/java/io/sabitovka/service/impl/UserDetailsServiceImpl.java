package io.sabitovka.service.impl;

import io.sabitovka.auth.entity.UserDetails;
import io.sabitovka.enums.ErrorCode;
import io.sabitovka.exception.ApplicationException;
import io.sabitovka.model.User;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.service.UserDetailsService;

/**
 * Интерфейс для обертки информации о пользователе в {@link UserDetails}. Реализует интерфейс {@link UserDetailsService}
 */
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND, "Пользователь с ID = %d не найден"
                        .formatted(id)));

        return new UserDetails(user.getId(), user.getName(), user.isAdmin());
    }
}
