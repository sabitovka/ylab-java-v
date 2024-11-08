package io.sabitovka.habittracker.service.impl;

import io.sabitovka.auditlogging.annotation.Audit;
import io.sabitovka.auditlogging.annotation.IgnoreAudit;
import io.sabitovka.habittracker.auth.AuthInMemoryContext;
import io.sabitovka.habittracker.auth.entity.UserDetails;
import io.sabitovka.habittracker.auth.util.PasswordHasher;
import io.sabitovka.habittracker.dto.user.ChangePasswordDto;
import io.sabitovka.habittracker.dto.user.CreateUserDto;
import io.sabitovka.habittracker.dto.user.UpdateUserDto;
import io.sabitovka.habittracker.dto.user.UserInfoDto;
import io.sabitovka.habittracker.enums.ErrorCode;
import io.sabitovka.habittracker.exception.ApplicationException;
import io.sabitovka.habittracker.model.User;
import io.sabitovka.habittracker.repository.HabitRepository;
import io.sabitovka.habittracker.repository.UserRepository;
import io.sabitovka.habittracker.service.UserService;
import io.sabitovka.habittracker.util.mapper.UserMapper;
import io.sabitovka.habittracker.util.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Интерфейс для управления пользователями. Реализует интерфейс {@link UserService}
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final PasswordHasher passwordHasher;
    private final UserMapper userMapper;

    @Audit(action = "Выполнено создание нового пользователя")
    @Override
    public UserInfoDto createUser(CreateUserDto createUserDto) {
        Validator.validate(createUserDto);
        if (userRepository.findUserByEmail(createUserDto.getEmail()).isPresent()) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST, "Данный email уже занят");
        }

        String hashedPassword = passwordHasher.hash(createUserDto.getPassword());
        createUserDto.setPassword(hashedPassword);

        User user = userMapper.createUserDtoToUser(createUserDto);
        User saved = userRepository.create(user);

        return userMapper.userToUserInfoDto(saved);
    }

    @Audit(action = "Выполнено обновление пользователя")
    @Override
    public void updateUser(Long userId, UpdateUserDto updateUserDto) {
        Validator.validate(updateUserDto);

        throwIfNotCurrentUserOrNotAdmin(userId);

        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND, "Не удалось обновить пользователя"));

        user.setName(updateUserDto.getName());
        user.setEmail(updateUserDto.getEmail());

        userRepository.update(user);
    }

    @Audit(action = "Выполнено обновление пароля пользователя")
    @Override
    public void changePassword(Long userId, @IgnoreAudit ChangePasswordDto changePasswordDto) {
        Validator.validate(changePasswordDto);

        throwIfNotCurrentUserOrNotAdmin(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND, "Не удалось обновить пароль пользователя"));

        if (!passwordHasher.verify(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST, "Старый пароль не совпадает");
        }

        String hashedPassword = passwordHasher.hash(changePasswordDto.getNewPassword());
        user.setPassword(hashedPassword);
        userRepository.update(user);
    }

    @Audit(action = "Выполнено удаление профиля пользователя")
    @Override
    public void deleteProfile(Long id) {
        throwIfNotCurrentUserOrNotAdmin(id);

        User user = userRepository.findById(id).orElseThrow();

        habitRepository.findAllByUserId(id).forEach(habitRepository::deleteWithHistoryByHabit);
        userRepository.deleteById(user.getId());
    }

    @Audit(action = "Выполнена блокировка пользователя")
    @Override
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND, "Не удалось заблокировать пользователя"));

        throwIfNotCurrentUserOrNotAdmin(userId);

        user.setActive(false);
        userRepository.update(user);
    }

    @Audit(action = "Выполнен поиск пользователя по ID")
    @Override
    public UserInfoDto findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        return userMapper.userToUserInfoDto(user);
    }

    @Audit(action = "Получены заблокированные пользователи")
    @Override
    public List<UserInfoDto> getBlockedUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.isActive())
                .map(userMapper::userToUserInfoDto)
                .toList();
    }

    @Audit(action = "Получены активные пользователи")
    @Override
    public List<UserInfoDto> getActiveUsers() {
        return userRepository.findAll().stream()
                .filter(User::isActive)
                .map(userMapper::userToUserInfoDto)
                .toList();
    }

    private void throwIfNotCurrentUserOrNotAdmin(Long userId) {
        UserDetails userDetails = AuthInMemoryContext.getContext().getAuthentication();
        if (!userDetails.getUserId().equals(userId) && !userDetails.isAdmin()) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }
    }

}
