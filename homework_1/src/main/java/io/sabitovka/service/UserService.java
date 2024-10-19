package io.sabitovka.service;

import io.sabitovka.dto.UserInfoDto;
import io.sabitovka.model.User;

import java.util.List;

public interface UserService {
    User createUser(UserInfoDto userInfoDto);
    void updateUser(UserInfoDto userInfoDto);
    void changePassword(UserInfoDto userInfoDto, String oldPassword);
    void deleteProfile(Long profileId, String password);
    void blockUser(Long userId);
    UserInfoDto findById(Long userId);
    List<UserInfoDto> getBlockedUsers();
    List<UserInfoDto> getActiveUsers();
    User mapUserInfoToUser(UserInfoDto userInfoDto);
    UserInfoDto mapUserToUserInfo(User user);
}
