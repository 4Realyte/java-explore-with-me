package ru.practicum.ewmservice.entities.location.mapper;

import org.mapstruct.*;
import ru.practicum.ewmservice.entities.location.dto.*;
import ru.practicum.ewmservice.entities.location.model.Location;
import ru.practicum.ewmservice.entities.location.model.LocationState;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location dtoToLocation(LocationRequestDto dto);

    Location newDtoToLocation(NewLocationDto dto);

    LocationFullDto toFullDto(Location location);

    List<LocationFullDto> toFullDto(List<Location> locations);

    LocationShortDto toDto(Location location);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLocation(LocationRequestDto dto, @MappingTarget Location location);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLocation(LocationUpdateRequestDto dto, @MappingTarget Location location);

    @ValueMappings({
            @ValueMapping(source = "CONFIRM_LOCATION", target = "CONFIRMED"),
            @ValueMapping(source = "REJECT_LOCATION", target = "REJECTED")
    })
    LocationState toLocationState(LocationStateAction action);
}
