package ru.practicum.ewmservice.entities.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.entities.location.dao.LocationRepository;
import ru.practicum.ewmservice.entities.location.dto.LocationRequestDto;
import ru.practicum.ewmservice.entities.location.mapper.LocationMapper;
import ru.practicum.ewmservice.entities.location.model.Location;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl {
    private final LocationRepository repository;
    private final LocationMapper mapper;

    @Transactional
    public Location addLocation(LocationRequestDto dto) {
        return repository.saveAndFlush(mapper.dtoToLocation(dto));
    }

    public Location updateLocationById(Long id, LocationRequestDto dto) {
        Location location = repository.findById(id).get();
        mapper.updateLocation(dto, location);
        return repository.save(location);
    }
}
