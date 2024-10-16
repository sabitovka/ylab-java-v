package io.sabitovka.service;

import io.sabitovka.common.Constants;
import io.sabitovka.dto.UserInfoDto;
import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.User;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.util.PasswordHasher;

import java.util.List;
import java.util.stream.Collectors;

public class UserService {

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;

    public UserService(UserRepository userRepository, HabitRepository habitRepository) {
        this.userRepository = userRepository;
        this.habitRepository = habitRepository;
    }

    public UserInfoDto mapUserToUserInfoDto(User user) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setId(user.getId());
        userInfoDto.setName(user.getName());
        userInfoDto.setEmail(user.getEmail());
        userInfoDto.setPassword(user.getPassword());
        userInfoDto.setIsAdmin(user.isAdmin());
        userInfoDto.setActive(user.isActive());
        return userInfoDto;
    }

    public User mapUserInfoDtoToUser(UserInfoDto userInfoDto) {
        User user = new User();
        user.setId(userInfoDto.getId());
        user.setName(userInfoDto.getName());
        user.setEmail(userInfoDto.getEmail());
        user.setPassword(userInfoDto.getPassword());
        user.setAdmin(userInfoDto.isAdmin());
        user.setActive(userInfoDto.isActive());
        return user;
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

    public User createUser(UserInfoDto userInfoDto) {
        validateRegistrationInput(userInfoDto);

        if (userRepository.findUserByEmail(userInfoDto.getEmail()).isPresent()) {
            throw new EntityAlreadyExistsException("Данный email уже занят");
        }

        String hashedPassword = PasswordHasher.hash(userInfoDto.getPassword());
        userInfoDto.setPassword(hashedPassword);

        User user = mapUserInfoDtoToUser(userInfoDto);
        return userRepository.create(user);
    }

    public void updateUser(UserInfoDto userInfoDto) {
        validateRegistrationInput(userInfoDto);

        User user = mapUserInfoDtoToUser(userInfoDto);
        userRepository.update(user);
    }

    public void changePassword(UserInfoDto userInfoDto, String oldPassword) {
        validateRegistrationInput(userInfoDto);

        User user = userRepository.findById(userInfoDto.getId()).orElseThrow();
        if (!PasswordHasher.verify(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Старый пароль не совпадает");
        }

        User updatedUser = mapUserInfoDtoToUser(userInfoDto);
        updatedUser.setPassword(PasswordHasher.hash(updatedUser.getPassword()));
        userRepository.update(updatedUser);
    }

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

    public void blockUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Не удалось найти пользователя"));
        user.setActive(false);
        userRepository.update(user);
    }

    public UserInfoDto findById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Не удалось найти пользователя"));
        return mapUserToUserInfoDto(user);
    }

    public List<UserInfoDto> getBlockedUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.isActive())
                .map(this::mapUserToUserInfoDto)
                .collect(Collectors.toList());
    }

    public List<UserInfoDto> getActiveUsers() {
        return userRepository.findAll().stream()
                .filter(User::isActive)
                .map(this::mapUserToUserInfoDto)
                .collect(Collectors.toList());
    }
}
