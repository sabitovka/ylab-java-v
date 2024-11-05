package io.sabitovka.util.mapper;

import io.sabitovka.dto.user.CreateUserDto;
import io.sabitovka.dto.user.UserInfoDto;
import io.sabitovka.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User createUserDtoToUser(CreateUserDto createUserDto);
    UserInfoDto userToUserInfoDto(User user);
}
