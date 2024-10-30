package io.sabitovka.service.impl;

import io.sabitovka.common.Constants;
import io.sabitovka.dto.user.CreateUserDto;
import io.sabitovka.dto.user.UserInfoDto;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.User;
import io.sabitovka.repository.HabitRepository;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.service.UserService;
import io.sabitovka.auth.util.PasswordHasher;
import io.sabitovka.util.mapper.UserMapper;
import io.sabitovka.util.validation.Validator;

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
//        user.setPassword(userInfoDto.getPassword());
//        user.setAdmin(userInfoDto.isAdmin());
//        user.setActive(userInfoDto.isActive());
        return user;
    }

    @Override
    public UserInfoDto mapUserToUserInfo(User user) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setId(user.getId());
        userInfoDto.setName(user.getName());
        userInfoDto.setEmail(user.getEmail());
//        userInfoDto.setPassword(user.getPassword());
//        userInfoDto.setAdmin(user.isAdmin());
//        userInfoDto.setActive(user.isActive());
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
//        if (userInfoDto.getPassword() == null || !userInfoDto.getPassword().matches(Constants.PASSWORD_REGEX)) {
//            throw new IllegalArgumentException("Пароль не соответствует требованиям");
//        }
    }

    @Override
    public UserInfoDto createUser(CreateUserDto createUserDto) {
        Validator.validate(createUserDto);
        if (userRepository.findUserByEmail(createUserDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Данный email уже занят");
        }

        String hashedPassword = PasswordHasher.hash(createUserDto.getPassword());
        createUserDto.setPassword(hashedPassword);

        User user = UserMapper.INSTANCE.createUserDtoToUser(createUserDto);
        User saved = userRepository.create(user);

        return UserMapper.INSTANCE.userToUserInfoDto(saved);
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
