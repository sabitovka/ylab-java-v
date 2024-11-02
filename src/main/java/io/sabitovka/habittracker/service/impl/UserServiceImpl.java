package io.sabitovka.habittracker.service.impl;

import io.sabitovka.habittracker.common.Constants;
import io.sabitovka.habittracker.dto.UserInfoDto;
import io.sabitovka.habittracker.exception.EntityNotFoundException;
import io.sabitovka.habittracker.model.User;
import io.sabitovka.habittracker.repository.HabitRepository;
import io.sabitovka.habittracker.repository.UserRepository;
import io.sabitovka.habittracker.service.UserService;
import io.sabitovka.habittracker.util.PasswordHasher;

import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;


    public UserServiceImpl(UserRepository userRepository, HabitRepository habitRepository) {
        this.userRepository = userRepository;
        this.habitRepository = habitRepository;
    }

    @Override
    public User mapUserInfoToUser(UserInfoDto userInfoDto) {
        User user = new User();
        user.setId(userInfoDto.getId());
        user.setName(userInfoDto.getName());
        user.setEmail(userInfoDto.getEmail());
        user.setPassword(userInfoDto.getPassword());
        user.setAdmin(userInfoDto.isAdmin());
        user.setActive(userInfoDto.isActive());
        return user;
    }

    @Override
    public UserInfoDto mapUserToUserInfo(User user) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setId(user.getId());
        userInfoDto.setName(user.getName());
        userInfoDto.setEmail(user.getEmail());
        userInfoDto.setPassword(user.getPassword());
        userInfoDto.setAdmin(user.isAdmin());
        userInfoDto.setActive(user.isActive());
        return userInfoDto;
    }

    private void validateRegistrationInput(UserInfoDto userInfoDto) {
        if (userInfoDto == null) {
            throw new IllegalArgumentException("Userinfo in null");
        }

        if (userInfoDto.getName() == null || userInfoDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (userInfoDto.getEmail() == null || !userInfoDto.getEmail().matches(Constants.EMAIL_REGEX)) {
            throw new IllegalArgumentException("Неправильный формат email");
        }
        if (userInfoDto.getPassword() == null || !userInfoDto.getPassword().matches(Constants.PASSWORD_REGEX)) {
            throw new IllegalArgumentException("Пароль не соответствует требованиям");
        }
    }

    @Override
    public User createUser(UserInfoDto userInfoDto) {
        validateRegistrationInput(userInfoDto);

        if (userRepository.findUserByEmail(userInfoDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Данный email уже занят");
        }

        String hashedPassword = PasswordHasher.hash(userInfoDto.getPassword());
        userInfoDto.setPassword(hashedPassword);

        User user = mapUserInfoToUser(userInfoDto);
        return userRepository.create(user);
    }

    @Override
    public void updateUser(UserInfoDto userInfoDto) {
        validateRegistrationInput(userInfoDto);

        User user = mapUserInfoToUser(userInfoDto);
        userRepository.update(user);
    }

    @Override
    public void changePassword(UserInfoDto userInfoDto, String oldPassword) {
        validateRegistrationInput(userInfoDto);

        User user = userRepository.findById(userInfoDto.getId()).orElseThrow();
        if (!PasswordHasher.verify(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Старый пароль не совпадает");
        }

        User updatedUser = mapUserInfoToUser(userInfoDto);
        updatedUser.setPassword(PasswordHasher.hash(updatedUser.getPassword()));
        userRepository.update(updatedUser);
    }

    @Override
    public void deleteProfile(Long profileId, String password) {
        if (!password.matches(Constants.PASSWORD_REGEX)) {
            throw new IllegalArgumentException("Пароль не соответствует требованиям");
        }

        User user = userRepository.findById(profileId).orElseThrow();
        if (PasswordHasher.verify(password, user.getPassword())) {
            userRepository.deleteById(profileId);
            habitRepository.findAllByUser(user).forEach(habit -> habitRepository.deleteById(habit.getId()));
        }
    }

    @Override
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId));
        user.setActive(false);
        userRepository.update(user);
    }

    @Override
    public UserInfoDto findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId));
        return mapUserToUserInfo(user);
    }

    @Override
    public List<UserInfoDto> getBlockedUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.isActive())
                .map(this::mapUserToUserInfo)
                .toList();
    }

    @Override
    public List<UserInfoDto> getActiveUsers() {
        return userRepository.findAll().stream()
                .filter(User::isActive)
                .map(this::mapUserToUserInfo)
                .toList();
    }
}
