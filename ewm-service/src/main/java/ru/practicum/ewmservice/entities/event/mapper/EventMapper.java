package ru.practicum.ewmservice.entities.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewmservice.entities.event.dto.EventFullDto;
import ru.practicum.ewmservice.entities.event.dto.EventShortDto;
import ru.practicum.ewmservice.entities.event.dto.NewEventDto;
import ru.practicum.ewmservice.entities.event.model.Event;
import ru.practicum.ewmservice.entities.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event dtoToEvent(NewEventDto dto, User initiator);

    EventFullDto toFullDto(Event event);

    EventShortDto toShortDto(Event event);

    List<EventShortDto> toShortDto(List<Event> events);
}
