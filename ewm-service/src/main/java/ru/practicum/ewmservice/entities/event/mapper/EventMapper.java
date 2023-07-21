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

    default void updateEvent(UpdateEventUserRequest dto, Event event, Category category, Location location) {
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
        if (location != null) {
            event.setLocation(location);
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
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    break;
            }
        }
    }

   /* @ValueMappings({
            @ValueMapping(source = "CANCEL_REVIEW", target = "CANCELED"),
            @ValueMapping(source = "SEND_TO_REVIEW", target = "PENDING")
    })
    EventState convertState(EventStateAction stateAction);*/
}
