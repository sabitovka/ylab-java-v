package io.sabitovka.habittracker.service.impl;

import io.sabitovka.habittracker.auth.entity.UserDetails;
import io.sabitovka.habittracker.enums.ErrorCode;
import io.sabitovka.habittracker.exception.ApplicationException;
import io.sabitovka.habittracker.model.User;
import io.sabitovka.habittracker.repository.UserRepository;
import io.sabitovka.habittracker.service.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Интерфейс для обертки информации о пользователе в {@link UserDetails}. Реализует интерфейс {@link UserDetailsService}
 */
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND, "Пользователь с ID = %d не найден"
                        .formatted(id)));

        return new UserDetails(user.getId(), user.getName(), user.isAdmin());
    }
}
