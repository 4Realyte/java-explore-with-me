package ru.practicum.ewmservice.controllers.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmservice.entities.event.dto.EventFullDto;
import ru.practicum.ewmservice.entities.event.dto.NewEventDto;
import ru.practicum.ewmservice.entities.event.service.EventServiceImpl;

import javax.validation.Valid;

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
}
