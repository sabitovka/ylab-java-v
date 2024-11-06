package io.sabitovka.util.mapper;

import io.sabitovka.dto.user.CreateUserDto;
import io.sabitovka.dto.user.UserInfoDto;
import io.sabitovka.model.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {
    User createUserDtoToUser(CreateUserDto createUserDto);
    UserInfoDto userToUserInfoDto(User user);
}
