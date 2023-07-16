package ru.practicum.ewmservice.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewmservice.user.dto.NewUserRequest;
import ru.practicum.ewmservice.user.dto.UserDto;
import ru.practicum.ewmservice.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User dtoToUser(NewUserRequest dto);

    UserDto toUserDto(User user);

    List<UserDto> toUserDto(List<User> users);
}
