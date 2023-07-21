package ru.practicum.ewmservice.entities.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewmservice.entities.user.dto.NewUserRequest;
import ru.practicum.ewmservice.entities.user.dto.UserDto;
import ru.practicum.ewmservice.entities.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User dtoToUser(NewUserRequest dto);

    UserDto toUserDto(User user);

    List<UserDto> toUserDto(List<User> users);
}
