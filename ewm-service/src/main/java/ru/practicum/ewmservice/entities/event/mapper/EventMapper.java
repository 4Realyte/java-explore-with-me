package ru.practicum.ewmservice.entities.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewmservice.entities.event.dto.EventFullDto;
import ru.practicum.ewmservice.entities.event.dto.NewEventDto;
import ru.practicum.ewmservice.entities.event.model.Event;
import ru.practicum.ewmservice.entities.user.model.User;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event dtoToEvent(NewEventDto dto, User initiator);

    EventFullDto toFullDto(Event event);
}
