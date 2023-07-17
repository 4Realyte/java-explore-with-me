package ru.practicum.ewmservice.entities.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewmservice.entities.category.mapper.CategoryMapper;
import ru.practicum.ewmservice.entities.category.model.Category;
import ru.practicum.ewmservice.entities.event.dto.EventFullDto;
import ru.practicum.ewmservice.entities.event.dto.EventShortDto;
import ru.practicum.ewmservice.entities.event.dto.NewEventDto;
import ru.practicum.ewmservice.entities.event.dto.UpdateEventUserRequest;
import ru.practicum.ewmservice.entities.event.model.Event;
import ru.practicum.ewmservice.entities.event.model.EventState;
import ru.practicum.ewmservice.entities.user.mapper.UserMapper;
import ru.practicum.ewmservice.entities.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class})
public interface EventMapper {
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "category", source = "category")
    @Mapping(ignore = true, target = "id")
    Event dtoToEvent(NewEventDto dto, User initiator, Category category);

    EventFullDto toFullDto(Event event);

    EventShortDto toShortDto(Event event);

    List<EventShortDto> toShortDto(List<Event> events);

    default void updateEvent(UpdateEventUserRequest dto, Event event, Category category) {
        if (dto == null) {
            return;
        }
        if (category != null) {
            event.setCategory(category);
        }
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocation(dto.getLocation());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
            }
        }
    }

   /* @ValueMappings({
            @ValueMapping(source = "CANCEL_REVIEW", target = "CANCELED"),
            @ValueMapping(source = "SEND_TO_REVIEW", target = "PENDING")
    })
    EventState convertState(EventStateAction stateAction);*/
}
