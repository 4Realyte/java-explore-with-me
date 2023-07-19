package ru.practicum.ewmservice.controllers.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.entities.event.dto.EventFullDto;
import ru.practicum.ewmservice.entities.event.dto.EventShortDto;
import ru.practicum.ewmservice.entities.event.dto.NewEventDto;
import ru.practicum.ewmservice.entities.event.dto.UpdateEventUserRequest;
import ru.practicum.ewmservice.entities.event.service.EventService;
import ru.practicum.ewmservice.entities.participation.dto.ParticipationResponseDto;
import ru.practicum.ewmservice.entities.participation.dto.ParticipationUpdateRequest;
import ru.practicum.ewmservice.entities.participation.dto.ParticipationUpdateResponse;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}")
public class PrivateEventController {
    private final EventService service;

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable("userId") Long userId, @Valid @RequestBody NewEventDto dto) {
        return service.addEvent(userId, dto);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationResponseDto addRequest(@PathVariable("userId") Long userId, @RequestParam("eventId") Long eventId) {
        return service.addRequest(userId, eventId);
    }


    @GetMapping("/events")
    public List<EventShortDto> getAllUserEvents(@PathVariable("userId") Long userId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        return service.getAllUserEvents(userId, from, size);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getInitiatorEvent(@PathVariable("userId") Long userId,
                                          @PathVariable("eventId") Long eventId) {
        return service.getEventByInitiator(userId, eventId);
    }

    @GetMapping("/requests")
    public List<ParticipationResponseDto> getUserRequestsInOtherEvents(@PathVariable("userId") Long userId) {
        return service.getUserRequestsInOtherEvents(userId);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable("userId") Long userId,
                                    @PathVariable("eventId") Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest dto) {
        return service.updateEvent(userId, eventId, dto);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationResponseDto> getCurrentUserRequests(@PathVariable("userId") Long userId,
                                                                 @PathVariable("eventId") Long eventId) {
        return service.getCurrentUserRequests(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public ParticipationUpdateResponse updateParticipations(@PathVariable("userId") Long userId,
                                                            @PathVariable("eventId") Long eventId,
                                                            @RequestBody ParticipationUpdateRequest request) {
        return service.updateParticipations(userId, eventId, request);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationResponseDto cancelRequest(@PathVariable("userId") Long userId,
                                                  @PathVariable("requestId") Long requestId) {
        return service.cancelRequest(requestId, userId);
    }

}
