package ru.practicum.ewmservice.entities.user.service;

import ru.practicum.ewmservice.entities.user.dto.NewUserRequest;
import ru.practicum.ewmservice.entities.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest dto);

    List<UserDto> findUsers(List<Long> ids, int from, int size);

    void deleteUser(Long userId);
}
