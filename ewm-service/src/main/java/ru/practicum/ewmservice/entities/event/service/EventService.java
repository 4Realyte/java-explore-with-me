package ru.practicum.ewmservice.entities.event.service;

import ru.practicum.ewmservice.entities.event.dto.*;
import ru.practicum.ewmservice.entities.participation.dto.ParticipationResponseDto;
import ru.practicum.ewmservice.entities.participation.dto.ParticipationUpdateRequest;
import ru.practicum.ewmservice.entities.participation.dto.ParticipationUpdateResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(Long userId, NewEventDto dto);

    ParticipationResponseDto addRequest(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventUserRequest dto);

    ParticipationUpdateResponse updateParticipations(Long userId, Long eventId, ParticipationUpdateRequest request);

    ParticipationResponseDto cancelRequest(Long requestId, Long userId);

    List<EventShortDto> getAllUserEvents(Long userId, int from, int size);

    EventFullDto getEventByInitiator(Long userId, Long eventId);

    List<ParticipationResponseDto> getUserRequestsInOtherEvents(Long userId);

    List<ParticipationResponseDto> getCurrentUserRequests(Long userId, Long eventId);

    List<EventFullDto> adminSearchEvents(GetEventSearch request);

    EventFullDto getEventById(Long eventId, HttpServletRequest servletRequest);

    List<EventShortDto> publicSearchEvents(GetEventSearch request, HttpServletRequest servletRequest);
}
