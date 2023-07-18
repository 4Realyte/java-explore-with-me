package ru.practicum.ewmservice.entities.event.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.entities.category.dao.CategoryRepository;
import ru.practicum.ewmservice.entities.category.model.Category;
import ru.practicum.ewmservice.entities.event.dao.EventRepository;
import ru.practicum.ewmservice.entities.event.dto.*;
import ru.practicum.ewmservice.entities.event.mapper.EventMapper;
import ru.practicum.ewmservice.entities.event.model.Event;
import ru.practicum.ewmservice.entities.event.model.EventState;
import ru.practicum.ewmservice.entities.participation.dao.ParticipationRepository;
import ru.practicum.ewmservice.entities.participation.dto.*;
import ru.practicum.ewmservice.entities.participation.model.Participation;
import ru.practicum.ewmservice.entities.user.dao.UserRepository;
import ru.practicum.ewmservice.entities.user.model.User;
import ru.practicum.ewmservice.exception.model.*;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.ewmservice.entities.event.model.QEvent.event;

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
    public ParticipationResponseDto addRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s doesn't exist", userId)));
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));

        checkRequestConstraints(user, event);

        Participation.ParticipationBuilder builder = Participation.builder();
        builder.requester(user);
        builder.event(event);
        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0)) {
            builder.status(ParticipationStatus.CONFIRMED);
        } else {
            builder.status(ParticipationStatus.PENDING);
        }
        return mapper.toPartDto(partRepository.save(builder.build()));
    }

    @Transactional
    public ParticipationResponseDto cancelRequest(Long requestId, Long userId) {
        Participation participation = partRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new ParticipationNotFoundException(
                        String.format("Participation with id %s is not found", requestId)));

        participation.setStatus(ParticipationStatus.CANCELED);
        return mapper.toPartDto(partRepository.saveAndFlush(participation));
    }

    public List<ParticipationResponseDto> getUserRequestsInOtherEvents(Long userId) {
        return mapper.toPartDto(partRepository.findAllUserRequests(userId));
    }

    public List<ParticipationResponseDto> getCurrentUserRequests(Long userId, Long eventId) {
        Event event = repository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));

        return mapper.toPartDto(partRepository.findAllByEventId(event.getId()));
    }

    @Transactional
    public ParticipationUpdateResponse updateParticipations(Long userId, Long eventId, ParticipationUpdateRequest request) {
        Event event = repository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));

        Integer participantLimit = event.getParticipantLimit();
        List<Participation> requests = partRepository.findAllByIdIn(request.getRequestIds());

        if (participantLimit.equals(0) || !event.getRequestModeration()) {
            return mapper.toUpdateResponseDto(requests);
        }
        checkUpdateParticipationRequest(event, participantLimit);

        for (Participation participation : requests) {
            int count = participantLimit - event.getConfirmedRequests();
            if (!ParticipationStatus.PENDING.equals(participation.getStatus())) {
                throw new ConflictException(String.format("You can't update request with %s status", participation.getStatus()));
            }
            if (count > 0) {
                if (request.getStatus() == ParticipationActionStatus.CONFIRMED) {
                    participation.setStatus(ParticipationStatus.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                } else {
                    participation.setStatus(ParticipationStatus.REJECTED);
                }
            } else {
                participation.setStatus(ParticipationStatus.REJECTED);
            }
        }
        repository.save(event);
        return mapper.toUpdateResponseDto(partRepository.saveAll(requests));
    }

    public List<EventFullDto> searchEvents(GetEventSearch request) {
        List<Predicate> predicates = new ArrayList<>();

        if (!request.getUsers().isEmpty()) {
            predicates.add(event.initiator.id.in(request.getUsers()));
        }
        if (!request.getCategories().isEmpty()) {
            predicates.add(event.category.id.in(request.getCategories()));
        }
        if (!request.getStates().isEmpty()) {
            predicates.add(event.state.in(request.getStates()));
        }
        if (request.getRangeStart() != null) {
            predicates.add(event.eventDate.goe(request.getRangeStart()));
        }
        if (request.getRangeEnd() != null) {
            predicates.add(event.eventDate.loe(request.getRangeEnd()));
        }
        Pageable page = PageRequest.of(request.getFrom(), request.getSize());
        if (!predicates.isEmpty()) {
            return mapper.toFullDto(repository.findAll(ExpressionUtils.allOf(predicates), page).getContent());
        } else {
            return mapper.toFullDto(repository.findAll(page).getContent());
        }
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
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventUserRequest dto) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));
        Category category = null;
        if (dto.getCategory() != null) {
            category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id %s not found", dto.getCategory())));
        }
        if (dto.getStateAction() != null) {
            String message;
            switch (dto.getStateAction()) {
                case PUBLISH_EVENT:
                    message = "publish";
                    break;
                case REJECT_EVENT:
                    message = "reject";
                    break;
                default:
                    message = "";
            }
            if (!event.getState().equals(EventState.PENDING)) {
                throw new ConflictException(String.format("You can't %s event with %s status", message, event.getState()));
            }
        }
        mapper.updateEvent(dto, event, category);
        Event updated = repository.saveAndFlush(event);
        if (updated.getPublishedOn() != null && updated.getEventDate().isBefore(updated.getPublishedOn().plusHours(1))) {
            throw new ConflictException(String.format("Event date must be at least one hour after publish date"));
        }
        return mapper.toFullDto(updated);
    }

    private static void checkUpdateParticipationRequest(Event event, Integer participantLimit) {
        if (participantLimit.equals(event.getConfirmedRequests())) {
            throw new ConflictException(String.format("Event with id %s has reached participation limit", event.getId()));
        }
    }

    private void checkRequestConstraints(User user, Event event) {
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ConflictException(String.format("Event with id %s has reached participation limit", event.getId()));
        }
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException(String.format("Event with id %s is not published yet", event.getId()));
        }
        if (user.equals(event.getInitiator())) {
            throw new ConflictException(String.format("Initiator can't participate in his own event"));
        }
    }
}
