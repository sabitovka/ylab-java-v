package io.sabitovka.service.impl;

import io.sabitovka.auth.util.Jwt;
import io.sabitovka.auth.util.PasswordHasher;
import io.sabitovka.dto.user.UserLoginDto;
import io.sabitovka.enums.ErrorCode;
import io.sabitovka.exception.ApplicationException;
import io.sabitovka.model.User;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.service.AuthorizationService;
import io.sabitovka.util.validation.Validator;

import java.util.Optional;

/**
 *
 */
public class AuthorizationServiceImpl implements AuthorizationService {
    private final UserRepository userRepository;
    public AuthorizationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        Validator.validate(userLoginDto);

        Optional<User> user = userRepository.findUserByEmail(userLoginDto.getEmail());
        if (user.isEmpty() || !PasswordHasher.verify(userLoginDto.getPassword(), user.get().getPassword())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED, "Неверный логин или пароль");
        }

        if (!user.get().isActive()) {
            throw new ApplicationException(ErrorCode.FORBIDDEN, "Ваша учетная запись была заблокирована");
        }

        return Jwt.generate(user.get().getId());
    }
}
