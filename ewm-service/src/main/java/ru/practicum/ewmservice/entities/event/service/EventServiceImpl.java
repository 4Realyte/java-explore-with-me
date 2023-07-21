package ru.practicum.ewmservice.entities.event.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.entities.category.dao.CategoryRepository;
import ru.practicum.ewmservice.entities.category.model.Category;
import ru.practicum.ewmservice.entities.event.dao.EventRepository;
import ru.practicum.ewmservice.entities.event.dto.*;
import ru.practicum.ewmservice.entities.event.mapper.EventMapper;
import ru.practicum.ewmservice.entities.event.model.Event;
import ru.practicum.ewmservice.entities.event.model.EventState;
import ru.practicum.ewmservice.entities.location.dto.LocationRequestDto;
import ru.practicum.ewmservice.entities.location.model.Location;
import ru.practicum.ewmservice.entities.location.service.LocationServiceImpl;
import ru.practicum.ewmservice.entities.participation.dao.ParticipationRepository;
import ru.practicum.ewmservice.entities.participation.dto.*;
import ru.practicum.ewmservice.entities.participation.model.Participation;
import ru.practicum.ewmservice.entities.user.dao.UserRepository;
import ru.practicum.ewmservice.entities.user.model.User;
import ru.practicum.ewmservice.exception.model.*;
import ru.practicum.ewmservice.stats.StatsService;
import stats.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewmservice.entities.event.model.QEvent.event;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRepository partRepository;
    private final EventMapper mapper;
    private final StatsService statsService;
    private final LocationServiceImpl locationService;


    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s doesn't exist", userId)));
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id %s not found", dto.getCategory())));
        Location location = locationService.addLocation(dto.getLocation());

        Event event = mapper.dtoToEvent(dto, user, category, location);

        return mapper.toFullDto(repository.saveAndFlush(event));
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
        Location location = null;
        if (dto.getLocation() != null) {
            location = locationService.updateLocationById(event.getLocation().getId(), dto.getLocation());
        }
        mapper.updateEvent(dto, event, category, location);

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
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConflictException(String.format("You can't publish/reject event with %s status", event.getState()));
        }
        Location location = null;
        if (dto.getLocation() != null) {
            location = locationService.updateLocationById(event.getLocation().getId(), dto.getLocation());
        }
        mapper.updateEvent(dto, event, category, location);
        Event updated = repository.saveAndFlush(event);
        if (updated.getPublishedOn() != null && updated.getEventDate().isBefore(updated.getPublishedOn().plusHours(1))) {
            throw new ConflictException(String.format("Event date must be at least one hour after publish date"));
        }
        return mapper.toFullDto(updated);
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
        if (participantLimit.equals(event.getConfirmedRequests())) {
            throw new ConflictException(String.format("Event with id %s has reached participation limit", event.getId()));
        }
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
        repository.saveAndFlush(event);
        return mapper.toUpdateResponseDto(partRepository.saveAll(requests));
    }

    @Transactional
    public ParticipationResponseDto cancelRequest(Long requestId, Long userId) {
        Participation participation = partRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new ParticipationNotFoundException(
                        String.format("Participation with id %s is not found", requestId)));

        participation.setStatus(ParticipationStatus.CANCELED);
        return mapper.toPartDto(partRepository.saveAndFlush(participation));
    }

    public List<EventShortDto> getAllUserEvents(Long userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s doesn't exist", userId)));

        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);

        return mapper.toShortDto(repository.findAllByInitiatorId(user.getId(), page));
    }

    public EventFullDto getEventByInitiator(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s doesn't exist", userId)));
        return mapper.toFullDto(repository.findByIdAndInitiatorId(eventId, user.getId())
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", userId))));
    }

    public List<ParticipationResponseDto> getUserRequestsInOtherEvents(Long userId) {
        return mapper.toPartDto(partRepository.findAllUserRequests(userId));
    }

    public List<ParticipationResponseDto> getCurrentUserRequests(Long userId, Long eventId) {
        Event event = repository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));

        return mapper.toPartDto(partRepository.findAllByEventId(event.getId()));
    }

    public List<EventFullDto> adminSearchEvents(GetEventSearch request) {
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

    public EventFullDto getEventById(Long eventId, HttpServletRequest servletRequest) {
        Event event = repository.findPublishedEvent(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));

        EventFullDto fullDto = mapper.toFullDto(event);

        Optional<ViewStats> stats = statsService.getStatBySingleUri(servletRequest, event.getPublishedOn());
        if (stats.isPresent()) {
            int views = stats.get().getHits();
            fullDto.setViews(views);
        }
        statsService.makeHit(servletRequest);
        return fullDto;
    }

    public List<EventShortDto> publicSearchEvents(GetEventSearch request, HttpServletRequest servletRequest) {
        statsService.makeHit(servletRequest);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(event.state.eq(EventState.PUBLISHED));
        checkSearchParameters(request, predicates);

        List<Event> events;
        EventSort sort = request.getSort();
        if (sort == EventSort.EVENT_DATE) {
            events = repository.findAll(ExpressionUtils.allOf(predicates),
                    PageRequest.of(request.getFrom(), request.getSize(), Sort.by(Sort.Direction.DESC, "eventDate"))).getContent();
        } else {
            events = repository.findAll(ExpressionUtils.allOf(predicates), PageRequest.of(request.getFrom(), request.getSize())).getContent();
        }
        Map<Long, Integer> viewMap = statsService.getHits(events);
        List<EventShortDto> responseDto = mapper.toShortDto(events);

        for (EventShortDto eventShortDto : responseDto) {
            eventShortDto.setViews(viewMap.getOrDefault(eventShortDto.getId(), 0));
        }

        if (sort == EventSort.VIEWS) {
            return responseDto.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        } else {
            return responseDto;
        }
    }

    private static void checkSearchParameters(GetEventSearch request, List<Predicate> predicates) {
        if (!request.getCategories().isEmpty()) {
            predicates.add(event.category.id.in(request.getCategories()));
        }
        String text = request.getText();
        if (text != null && !text.isBlank()) {
            predicates.add(event.annotation.containsIgnoreCase(text).or(event.description.containsIgnoreCase(text)));
        }
        if (request.getPaid() != null) {
            predicates.add(event.paid.eq(request.getPaid()));
        }
        if (request.getHasRange()) {
            if (request.getRangeStart() != null) {
                predicates.add(event.eventDate.goe(request.getRangeStart()));
            }
            if (request.getRangeEnd() != null) {
                predicates.add(event.eventDate.loe(request.getRangeEnd()));
            }
        } else {
            predicates.add(event.eventDate.goe(LocalDateTime.now()));
        }
        if (request.getOnlyAvailable()) {
            predicates.add(event.participantLimit.gt(event.confirmedRequests));
        }
        LocationRequestDto location = request.getLocation();
        if (location != null) {
            BooleanExpression expression = Expressions.numberTemplate(Float.class,
                            "distance({0},{1},{2},{3})",
                            event.location.lat, event.location.lon, location.getLat(), location.getLon())
                    .subtract(location.getRad())
                    .loe(0);
            predicates.add(expression);
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
