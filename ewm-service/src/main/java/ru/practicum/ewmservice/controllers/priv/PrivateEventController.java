package ru.practicum.ewmservice.controllers.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.entities.event.dto.EventFullDto;
import ru.practicum.ewmservice.entities.event.dto.EventShortDto;
import ru.practicum.ewmservice.entities.event.dto.NewEventDto;
import ru.practicum.ewmservice.entities.event.dto.UpdateEventUserRequest;
import ru.practicum.ewmservice.entities.event.service.EventServiceImpl;
import ru.practicum.ewmservice.entities.participation.dto.ParticipationResponseDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}")
public class PrivateEventController {
    private final EventServiceImpl service;

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
    public List<EventShortDto> getEvents(@PathVariable("userId") Long userId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        return service.getEvents(userId, from, size);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getInitiatorEvent(@PathVariable("userId") Long userId,
                                          @PathVariable("eventId") Long eventId) {
        return service.getInitiatorEvent(userId, eventId);
    }

    @GetMapping("/requests")
    public List<ParticipationResponseDto> getUserRequests(@PathVariable("userId") Long userId) {
        return service.getUserRequests(userId);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable("userId") Long userId,
                                    @PathVariable("eventId") Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest dto) {
        return service.updateEvent(userId, eventId, dto);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationResponseDto cancelRequest(@PathVariable("userId") Long userId,
                                                  @PathVariable("requestId") Long requestId) {
        return service.cancelRequest(requestId, userId);
    }

}
