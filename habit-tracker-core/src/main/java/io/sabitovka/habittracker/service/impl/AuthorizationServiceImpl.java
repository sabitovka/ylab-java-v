package io.sabitovka.habittracker.service.impl;

import io.sabitovka.habittracker.annotation.Audit;
import io.sabitovka.habittracker.auth.util.Jwt;
import io.sabitovka.habittracker.auth.util.PasswordHasher;
import io.sabitovka.habittracker.dto.user.UserLoginDto;
import io.sabitovka.habittracker.enums.ErrorCode;
import io.sabitovka.habittracker.exception.ApplicationException;
import io.sabitovka.habittracker.model.User;
import io.sabitovka.habittracker.repository.UserRepository;
import io.sabitovka.habittracker.service.AuthorizationService;
import io.sabitovka.habittracker.util.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для выполнения авторизации в системе. Реализует интерфейс {@link AuthorizationService}
 */
@RequiredArgsConstructor
@Service
public class AuthorizationServiceImpl implements AuthorizationService {
    private final UserRepository userRepository;
    private final Jwt jwt;
    private final PasswordHasher passwordHasher;

    @Audit(action = "Выполнен вход в систему")
    @Override
    public String login(UserLoginDto userLoginDto) {
        Validator.validate(userLoginDto);

        Optional<User> user = userRepository.findUserByEmail(userLoginDto.getEmail());
        if (user.isEmpty() || !passwordHasher.verify(userLoginDto.getPassword(), user.get().getPassword())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED, "Неверный логин или пароль");
        }

        if (!user.get().isActive()) {
            throw new ApplicationException(ErrorCode.FORBIDDEN, "Ваша учетная запись была заблокирована");
        }

        return jwt.generate(user.get().getId());
    }
}
