package ru.practicum.ewmservice.entities.location.service;

import ru.practicum.ewmservice.entities.location.dto.LocationFullDto;
import ru.practicum.ewmservice.entities.location.dto.LocationRequestDto;
import ru.practicum.ewmservice.entities.location.dto.LocationUpdateRequestDto;
import ru.practicum.ewmservice.entities.location.dto.NewLocationDto;
import ru.practicum.ewmservice.entities.location.model.Location;

import java.util.List;

public interface LocationService {
    Location addLocationByUser(LocationRequestDto dto);

    LocationFullDto addLocationByAdmin(NewLocationDto dto);

    Location updateLocationById(Long id, LocationRequestDto dto);

    LocationFullDto updateLocationByAdmin(LocationUpdateRequestDto dto, Long locId);

    void deleteLocation(Long id);

    List<LocationFullDto> findLocations(int from, int size, Boolean onlyConfirmed, String name);
}
