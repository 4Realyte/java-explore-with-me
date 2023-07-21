package ru.practicum.ewmservice.entities.location.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewmservice.entities.location.dto.LocationRequestDto;
import ru.practicum.ewmservice.entities.location.dto.LocationResponseDto;
import ru.practicum.ewmservice.entities.location.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location dtoToLocation(LocationRequestDto dto);

    LocationResponseDto toDto(Location location);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLocation(LocationRequestDto dto, @MappingTarget Location location);
}
