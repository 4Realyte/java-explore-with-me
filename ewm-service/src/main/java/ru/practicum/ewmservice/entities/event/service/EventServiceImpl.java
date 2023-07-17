package ru.practicum.ewmservice.entities.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.entities.event.dao.EventRepository;
import ru.practicum.ewmservice.entities.event.dto.EventFullDto;
import ru.practicum.ewmservice.entities.event.dto.NewEventDto;
import ru.practicum.ewmservice.entities.event.mapper.EventMapper;
import ru.practicum.ewmservice.entities.event.model.Event;
import ru.practicum.ewmservice.entities.user.dao.UserRepository;
import ru.practicum.ewmservice.entities.user.model.User;
import ru.practicum.ewmservice.exception.UserNotFoundException;
import ru.practicum.statsclient.client.StatsClient;

@Service
@RequiredArgsConstructor
public class EventServiceImpl {
    private final EventRepository repository;
    private final UserRepository userRepository;
    private final EventMapper mapper;
    private final StatsClient client = new StatsClient("http://localhost:9090","ewm-service");


    public EventFullDto addEvent(Long userId, NewEventDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s doesn't exist", userId)));
        Event event = mapper.dtoToEvent(dto, user);

        return mapper.toFullDto(repository.save(event));
    }
}
