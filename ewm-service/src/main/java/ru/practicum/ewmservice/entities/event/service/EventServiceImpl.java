package ru.practicum.ewmservice.entities.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.entities.event.dao.EventRepository;
import ru.practicum.ewmservice.entities.event.dto.EventFullDto;
import ru.practicum.ewmservice.entities.event.dto.EventShortDto;
import ru.practicum.ewmservice.entities.event.dto.NewEventDto;
import ru.practicum.ewmservice.entities.event.mapper.EventMapper;
import ru.practicum.ewmservice.entities.event.model.Event;
import ru.practicum.ewmservice.entities.user.dao.UserRepository;
import ru.practicum.ewmservice.entities.user.model.User;
import ru.practicum.ewmservice.exception.EventNotFoundException;
import ru.practicum.ewmservice.exception.UserNotFoundException;
import ru.practicum.statsclient.client.StatsClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl {
    private final EventRepository repository;
    private final UserRepository userRepository;
    private final EventMapper mapper;
    private final StatsClient client = new StatsClient("http://localhost:9090", "ewm-service");

    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s doesn't exist", userId)));
        Event event = mapper.dtoToEvent(dto, user);

        return mapper.toFullDto(repository.save(event));
    }

    public List<EventShortDto> getEvents(Long userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s doesn't exist", userId)));

        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);

        return mapper.toShortDto(repository.findAllByInitiatorId(user.getId(), page));
    }

    public EventFullDto getInitiatorEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s doesn't exist", userId)));
        return mapper.toFullDto(repository.findByIdAndInitiatorId(eventId, user.getId())
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", userId))));
    }
}
