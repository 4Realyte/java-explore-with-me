package ru.practicum.ewmservice.entities.event.mapper;

import org.mapstruct.*;
import ru.practicum.ewmservice.entities.category.mapper.CategoryMapper;
import ru.practicum.ewmservice.entities.category.model.Category;
import ru.practicum.ewmservice.entities.event.dto.*;
import ru.practicum.ewmservice.entities.event.model.Event;
import ru.practicum.ewmservice.entities.event.model.EventState;
import ru.practicum.ewmservice.entities.location.mapper.LocationMapper;
import ru.practicum.ewmservice.entities.location.model.Location;
import ru.practicum.ewmservice.entities.participation.dto.ParticipationResponseDto;
import ru.practicum.ewmservice.entities.participation.dto.ParticipationStatus;
import ru.practicum.ewmservice.entities.participation.dto.ParticipationUpdateResponse;
import ru.practicum.ewmservice.entities.participation.model.Participation;
import ru.practicum.ewmservice.entities.user.mapper.UserMapper;
import ru.practicum.ewmservice.entities.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class, LocationMapper.class})
public interface EventMapper {
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "category", source = "category")
    @Mapping(ignore = true, target = "id")
    @Mapping(target = "location", source = "location")
    @Mapping(ignore = true, target = "state")
    Event dtoToEvent(NewEventDto dto, User initiator, Category category, Location location);

    EventFullDto toFullDto(Event event);

    List<EventFullDto> toFullDto(List<Event> events);

    EventShortDto toShortDto(Event event);

    List<EventShortDto> toShortDto(List<Event> events);

    @Mapping(source = "participation.event.id", target = "event")
    @Mapping(source = "participation.requester.id", target = "requester")
    ParticipationResponseDto toPartDto(Participation participation);

    List<ParticipationResponseDto> toPartDto(List<Participation> participations);


    default ParticipationUpdateResponse toUpdateResponseDto(List<Participation> requests) {
        Map<ParticipationStatus, List<ParticipationResponseDto>> dtos = requests
                .stream()
                .map(this::toPartDto)
                .collect(Collectors.groupingBy(ParticipationResponseDto::getStatus));
        return ParticipationUpdateResponse.builder()
                .confirmedRequests(dtos.getOrDefault(ParticipationStatus.CONFIRMED, Collections.emptyList()))
                .rejectedRequests(dtos.getOrDefault(ParticipationStatus.REJECTED, Collections.emptyList()))
                .build();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(source = "location", target = "event.location")
    @Mapping(source = "dto.stateAction", target = "event.state")
    @Mapping(target = "createdOn", ignore = true)
    void updateEvent(UpdateEventUserRequest dto, @MappingTarget Event event, Category category, Location location);

    @ValueMappings({
            @ValueMapping(source = "CANCEL_REVIEW", target = "CANCELED"),
            @ValueMapping(source = "REJECT_EVENT", target = "CANCELED"),
            @ValueMapping(source = "SEND_TO_REVIEW", target = "PENDING"),
            @ValueMapping(source = "PUBLISH_EVENT", target = "PUBLISHED")
    })
    EventState convertState(EventStateAction stateAction);
}
