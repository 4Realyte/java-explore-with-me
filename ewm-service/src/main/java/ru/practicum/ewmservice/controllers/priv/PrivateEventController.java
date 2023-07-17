package ru.practicum.ewmservice.controllers.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.entities.event.dto.EventFullDto;
import ru.practicum.ewmservice.entities.event.dto.EventShortDto;
import ru.practicum.ewmservice.entities.event.dto.NewEventDto;
import ru.practicum.ewmservice.entities.event.service.EventServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users")
public class PrivateEventController {
    private final EventServiceImpl service;

    @PostMapping("/{userId}/events")
    public EventFullDto addEvent(@PathVariable("userId") Long userId, @Valid NewEventDto dto) {
        return service.addEvent(userId, dto);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvents(@PathVariable("userId") Long userId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        return service.getEvents(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getInitiatorEvent(@PathVariable("userId") Long userId,
                                                @PathVariable("eventId") Long eventId) {
        return service.getInitiatorEvent(userId, eventId);
    }
}
