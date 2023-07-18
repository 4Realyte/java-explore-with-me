package ru.practicum.ewmservice.entities.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.entities.category.dao.CategoryRepository;
import ru.practicum.ewmservice.entities.category.model.Category;
import ru.practicum.ewmservice.entities.event.dao.EventRepository;
import ru.practicum.ewmservice.entities.event.dto.EventFullDto;
import ru.practicum.ewmservice.entities.event.dto.EventShortDto;
import ru.practicum.ewmservice.entities.event.dto.NewEventDto;
import ru.practicum.ewmservice.entities.event.dto.UpdateEventUserRequest;
import ru.practicum.ewmservice.entities.event.mapper.EventMapper;
import ru.practicum.ewmservice.entities.event.model.Event;
import ru.practicum.ewmservice.entities.event.model.EventState;
import ru.practicum.ewmservice.entities.participation.dao.ParticipationRepository;
import ru.practicum.ewmservice.entities.participation.dto.ParticipationResponseDto;
import ru.practicum.ewmservice.entities.participation.dto.ParticipationStatus;
import ru.practicum.ewmservice.entities.participation.dto.ParticipationUpdateRequest;
import ru.practicum.ewmservice.entities.participation.dto.ParticipationUpdateResponse;
import ru.practicum.ewmservice.entities.participation.model.Participation;
import ru.practicum.ewmservice.entities.user.dao.UserRepository;
import ru.practicum.ewmservice.entities.user.model.User;
import ru.practicum.ewmservice.exception.*;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl {
    private final EventRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRepository partRepository;
    private final EventMapper mapper;


    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s doesn't exist", userId)));
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id %s not found", dto.getCategory())));
        Event event = mapper.dtoToEvent(dto, user, category);

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

    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event event = repository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));
        Category category = null;
        if (dto.getCategory() != null) {
            category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id %s not found", dto.getCategory())));
        }
        if (EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException(String.format("You can't update event with %s status", EventState.PUBLISHED));
        }
        mapper.updateEvent(dto, event, category);

        return mapper.toFullDto(repository.save(event));
    }

    @Transactional
    public ParticipationResponseDto addRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s doesn't exist", userId)));
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));

        checkRequestConstraints(user, event);

        Participation.ParticipationBuilder builder = Participation.builder();
        builder.requester(user);
        builder.event(event);
        if (event.getRequestModeration()) {
            builder.status(ParticipationStatus.PENDING);
        } else {
            builder.status(ParticipationStatus.CONFIRMED);
        }
        return mapper.toPartDto(partRepository.save(builder.build()));
    }

    @Transactional
    public ParticipationResponseDto cancelRequest(Long requestId, Long userId) {
        Participation participation = partRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new ParticipationNotFoundException(
                        String.format("Participation with id %s is not found", requestId)));

        participation.setStatus(ParticipationStatus.REJECTED);
        return mapper.toPartDto(partRepository.saveAndFlush(participation));
    }

    public List<ParticipationResponseDto> getUserRequestsInOtherEvents(Long userId) {
        return mapper.toPartDto(partRepository.findAllUserRequests(userId));
    }

    private void checkRequestConstraints(User user, Event event) {
        if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ConflictException(String.format("Event with id %s has reached participation limit", event.getId()));
        }
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException(String.format("Event with id %s is not published yet", event.getId()));
        }
        if (user.equals(event.getInitiator())) {
            throw new ConflictException(String.format("Initiator can't participate in his own event"));
        }
    }

    public List<ParticipationResponseDto> getCurrentUserRequests(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s doesn't exist", userId)));
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));
        return mapper.toPartDto(partRepository.findAllByRequesterIdAndEventId(user.getId(), event.getId()));
    }

    @Transactional
    public ParticipationUpdateResponse updateParticipations(Long userId, Long eventId, ParticipationUpdateRequest request) {
        Event event = repository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));

        Integer participantLimit = event.getParticipantLimit();
        checkUpdateParticipationRequest(request, event, participantLimit);

        List<Participation> requests = partRepository.findAllByIdIn(request.getRequestIds());
        for (Participation participation : requests) {
            int count = participantLimit - event.getConfirmedRequests();
            if (!ParticipationStatus.PENDING.equals(participation.getStatus())) {
                throw new ConflictException(String.format("You can't update request with %s status", participation.getStatus()));
            }
            if (count > 0) {
                participation.setStatus(request.getStatus());
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            } else {
                participation.setStatus(ParticipationStatus.REJECTED);
            }
        }
        repository.save(event);
        return mapper.toUpdateResponseDto(partRepository.saveAll(requests));
    }

    private static void checkUpdateParticipationRequest(ParticipationUpdateRequest request, Event event, Integer participantLimit) {
        if (participantLimit.equals(event.getConfirmedRequests())) {
            throw new ConflictException(String.format("Event with id %s has reached participation limit", event.getId()));
        }
        if (!request.getStatus().equals(ParticipationStatus.PENDING)) {
            throw new BadRequestException("Request must have status PENDING");
        }
    }
}
