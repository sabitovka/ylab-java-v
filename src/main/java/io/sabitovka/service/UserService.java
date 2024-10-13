package io.sabitovka.service;

import io.sabitovka.common.Constants;
import io.sabitovka.dto.UserInfoDto;
import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.model.User;
import io.sabitovka.repository.UserRepository;
import io.sabitovka.util.PasswordHasher;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserInfoDto mapUserToUserInfoDto(User user) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setId(user.getId());
        userInfoDto.setName(user.getName());
        userInfoDto.setEmail(user.getEmail());
        userInfoDto.setPassword(user.getPassword());
        return userInfoDto;
    }

    public User mapUserInfoDtoToUser(UserInfoDto userInfoDto) {
        User user = new User();
        user.setId(userInfoDto.getId());
        user.setName(userInfoDto.getName());
        user.setEmail(userInfoDto.getEmail());
        user.setPassword(userInfoDto.getPassword());
        return user;
    }

    private void validateRegistrationInput(UserInfoDto userInfoDto) {
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

    public void createUser(UserInfoDto userInfoDto) {
        validateRegistrationInput(userInfoDto);

        if (userRepository.findUserByEmail(userInfoDto.getEmail()).isPresent()) {
            throw new EntityAlreadyExistsException("Данный email уже занят");
        }

        String hashedPassword = PasswordHasher.hash(userInfoDto.getPassword());
        userInfoDto.setPassword(hashedPassword);

        User user = mapUserInfoDtoToUser(userInfoDto);
        userRepository.create(user);
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
        }
    }
}
