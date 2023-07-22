package ru.practicum.ewmservice.entities.location.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewmservice.entities.location.dao.LocationRepository;
import ru.practicum.ewmservice.entities.location.dto.LocationFullDto;
import ru.practicum.ewmservice.entities.location.dto.LocationRequestDto;
import ru.practicum.ewmservice.entities.location.dto.LocationUpdateRequestDto;
import ru.practicum.ewmservice.entities.location.dto.NewLocationDto;
import ru.practicum.ewmservice.entities.location.mapper.LocationMapper;
import ru.practicum.ewmservice.entities.location.model.Location;
import ru.practicum.ewmservice.entities.location.model.LocationState;

import java.util.List;

import static ru.practicum.ewmservice.entities.location.model.QLocation.location;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl {
    private final LocationRepository repository;
    private final LocationMapper mapper;

    public Location addLocationByUser(LocationRequestDto dto) {
        Location location = mapper.dtoToLocation(dto);
        location.setState(LocationState.PENDING);
        return repository.save(location);
    }

    public LocationFullDto addLocationByAdmin(NewLocationDto dto) {
        Location location = mapper.newDtoToLocation(dto);
        location.setState(LocationState.CONFIRMED);
        return mapper.toFullDto(repository.save(location));
    }

    public Location updateLocationById(Long id, LocationRequestDto dto) {
        Location location = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));
        mapper.updateLocation(dto, location);
        return repository.save(location);
    }

    public LocationFullDto updateLocationByAdmin(LocationUpdateRequestDto dto, Long locId) {
        Location location = repository.findById(locId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));
        mapper.updateLocation(dto, location);
        return mapper.toFullDto(repository.save(location));
    }

    public void deleteLocation(Long id) {
        repository.deleteById(id);
    }

    public List<LocationFullDto> findLocations(int from, int size, Boolean onlyConfirmed, String name) {
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        BooleanBuilder builder = new BooleanBuilder();
        if (onlyConfirmed != null) {
            if (onlyConfirmed) {
                builder.and(location.state.eq(LocationState.CONFIRMED));
            }
        }
        if (name != null) {
            builder.and(location.locationName.containsIgnoreCase(name));
        }
        List<Location> content;
        if (builder.hasValue()) {
            content = repository.findAll(builder.getValue(), page).getContent();
        } else {
            content = repository.findAll(page).getContent();
        }
        return mapper.toFullDto(content);
    }
}
