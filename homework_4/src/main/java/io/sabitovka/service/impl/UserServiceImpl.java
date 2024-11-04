package io.sabitovka.service.impl;

import io.sabitovka.auth.AuthInMemoryContext;
import io.sabitovka.auth.entity.UserDetails;
import io.sabitovka.auth.util.PasswordHasher;
import io.sabitovka.dto.user.ChangePasswordDto;
import io.sabitovka.dto.user.CreateUserDto;
import io.sabitovka.dto.user.UpdateUserDto;
import io.sabitovka.dto.user.UserInfoDto;
import io.sabitovka.enums.ErrorCode;
import io.sabitovka.exception.ApplicationException;
import io.sabitovka.model.User;
import io.sabitovka.repository.FulfilledHabitRepository;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.service.UserService;
import io.sabitovka.util.mapper.UserMapper;
import io.sabitovka.util.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final FulfilledHabitRepository fulfilledHabitRepository;
    private final PasswordHasher passwordHasher;

    private void throwIfNotCurrentUserOrNotAdmin(Long userId) {
        UserDetails userDetails = AuthInMemoryContext.getContext().getAuthentication();
        if (!userDetails.getUserId().equals(userId) && !userDetails.isAdmin()) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }
    }

    @Override
    public UserInfoDto createUser(CreateUserDto createUserDto) {
        Validator.validate(createUserDto);
        if (userRepository.findUserByEmail(createUserDto.getEmail()).isPresent()) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST, "Данный email уже занят");
        }

        String hashedPassword = passwordHasher.hash(createUserDto.getPassword());
        createUserDto.setPassword(hashedPassword);

        User user = UserMapper.INSTANCE.createUserDtoToUser(createUserDto);
        User saved = userRepository.create(user);

        return UserMapper.INSTANCE.userToUserInfoDto(saved);
    }

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

    @Override
    public void changePassword(Long userId, ChangePasswordDto changePasswordDto) {
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

    @Override
    public void deleteProfile(Long id) {
        throwIfNotCurrentUserOrNotAdmin(id);

        User user = userRepository.findById(id).orElseThrow();

        // TODO: 30.10.2024 Перевести этот ужас на sql запросы
        habitRepository.findAllByUserId(id).forEach(habit -> {
            fulfilledHabitRepository.findAllByHabit(habit).forEach(fulfilledHabit -> fulfilledHabitRepository.deleteById(fulfilledHabit.getId()));
            habitRepository.deleteById(habit.getId());
        });
        userRepository.deleteById(user.getId());
    }

    @Override
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND, "Не удалось заблокировать пользователя"));

        throwIfNotCurrentUserOrNotAdmin(userId);

        user.setActive(false);
        userRepository.update(user);
    }

    @Override
    public UserInfoDto findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        return UserMapper.INSTANCE.userToUserInfoDto(user);
    }

    @Override
    public List<UserInfoDto> getBlockedUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.isActive())
                .map(UserMapper.INSTANCE::userToUserInfoDto)
                .toList();
    }

    @Override
    public List<UserInfoDto> getActiveUsers() {
        return userRepository.findAll().stream()
                .filter(User::isActive)
                .map(UserMapper.INSTANCE::userToUserInfoDto)
                .toList();
    }
}
