package io.sabitovka.habittracker.util.mapper;

import io.sabitovka.habittracker.dto.user.CreateUserDto;
import io.sabitovka.habittracker.dto.user.UserInfoDto;
import io.sabitovka.habittracker.model.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {
    User createUserDtoToUser(CreateUserDto createUserDto);
    UserInfoDto userToUserInfoDto(User user);
}
