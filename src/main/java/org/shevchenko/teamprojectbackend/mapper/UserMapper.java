package org.shevchenko.teamprojectbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.shevchenko.teamprojectbackend.config.MapperConfig;
import org.shevchenko.teamprojectbackend.dto.user.UserRegistrationRequestDto;
import org.shevchenko.teamprojectbackend.dto.user.UserResponseDto;
import org.shevchenko.teamprojectbackend.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    @Named("userToId")
    default Long userToId(User user) {
        return user.getId();
    }

    @Named("userFromId")
    default User userFromId(Long id) {
        return new User(id);
    }
}
