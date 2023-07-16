package ru.practicum.ewmservice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.exception.UserNotFoundException;
import ru.practicum.ewmservice.user.dao.UserRepository;
import ru.practicum.ewmservice.user.dto.NewUserRequest;
import ru.practicum.ewmservice.user.dto.UserDto;
import ru.practicum.ewmservice.user.mapper.UserMapper;
import ru.practicum.ewmservice.user.model.QUser;
import ru.practicum.ewmservice.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {
    private final UserRepository repository;
    private final UserMapper mapper;

    public UserDto addUser(NewUserRequest dto) {
        User newUser = mapper.dtoToUser(dto);
        return mapper.toUserDto(repository.save(newUser));
    }

    public List<UserDto> findUsers(List<Long> ids, int from, int size) {
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.ASC, "id"));
        QUser user = QUser.user;
        List<UserDto> dtos;
        if (!ids.isEmpty()) {
            dtos = mapper.toUserDto(repository.findAll(user.id.in(ids), page).getContent());
        } else {
            dtos = mapper.toUserDto(repository.findAll(page).getContent());
        }
        return dtos;
    }

    public void deleteUser(Long userId) {
        try {
            repository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("User with id %s doesn't exist", userId));
        }
    }
}
