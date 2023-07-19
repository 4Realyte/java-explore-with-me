package ru.practicum.ewmservice.controllers.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.entities.event.dto.EventFullDto;
import ru.practicum.ewmservice.entities.event.dto.EventShortDto;
import ru.practicum.ewmservice.entities.event.dto.GetEventSearch;
import ru.practicum.ewmservice.entities.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
public class PublicEventController {
    private final EventService service;

    @GetMapping
    public List<EventShortDto> findAllEvents(@RequestParam(required = false) String text,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(required = false) Boolean paid,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                             @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                             @RequestParam(required = false) String sort,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size,
                                             HttpServletRequest servletRequest) {
        return service.publicSearchEvents(GetEventSearch.of(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size), servletRequest);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable("id") Long id, HttpServletRequest servletRequest) {
        return service.getEventById(id, servletRequest);
    }
}
