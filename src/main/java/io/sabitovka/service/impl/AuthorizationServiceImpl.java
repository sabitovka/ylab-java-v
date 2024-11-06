package io.sabitovka.service.impl;

import io.sabitovka.annotation.Audit;
import io.sabitovka.auth.util.Jwt;
import io.sabitovka.auth.util.PasswordHasher;
import io.sabitovka.dto.user.UserLoginDto;
import io.sabitovka.enums.ErrorCode;
import io.sabitovka.exception.ApplicationException;
import io.sabitovka.model.User;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.service.AuthorizationService;
import io.sabitovka.util.validation.Validator;
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
