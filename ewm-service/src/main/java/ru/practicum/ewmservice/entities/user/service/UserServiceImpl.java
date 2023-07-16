package ru.practicum.ewmservice.entities.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.entities.user.dao.UserRepository;
import ru.practicum.ewmservice.entities.user.dto.NewUserRequest;
import ru.practicum.ewmservice.entities.user.dto.UserDto;
import ru.practicum.ewmservice.entities.user.mapper.UserMapper;
import ru.practicum.ewmservice.entities.user.model.User;
import ru.practicum.ewmservice.exception.UserNotFoundException;

import java.util.List;

import static ru.practicum.ewmservice.entities.user.model.QUser.user;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional
    public UserDto addUser(NewUserRequest dto) {
        User newUser = mapper.dtoToUser(dto);
        return mapper.toUserDto(repository.save(newUser));
    }

    public List<UserDto> findUsers(List<Long> ids, int from, int size) {
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.ASC, "id"));
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
