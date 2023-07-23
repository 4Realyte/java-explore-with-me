package ru.practicum.ewmservice.entities.location.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.ewmservice.entities.location.model.QLocation.location;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {
    private final LocationRepository repository;
    private final LocationMapper mapper;
    private final GeocodingService geocodingService;

    public Location addLocationByUser(LocationRequestDto dto) {
        Location location = mapper.dtoToLocation(dto);
        location.setState(LocationState.PENDING);
        location.setRad(dto.getRad() == null ? 0.4f : dto.getRad());
        return repository.save(location);
    }

    public LocationFullDto addLocationByAdmin(NewLocationDto dto) {
        String address = geocodingService.getAddress(dto.getLat(), dto.getLon());
        Location location = mapper.newDtoToLocation(dto);
        location.setState(LocationState.CONFIRMED);
        if (!address.isEmpty()) {
            location.setAddress(address);
        }
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
        Float lon = dto.getLon();
        Float lat = dto.getLat();
        Map<String, Float> coordinates = coordinatesToUpdate(lon, lat, location);
        if (!coordinates.isEmpty()) {
            String address = geocodingService.getAddress(
                    coordinates.getOrDefault("lat", location.getLat()),
                    coordinates.getOrDefault("lon", location.getLon()));
            if (!address.isEmpty()) {
                location.setAddress(address);
            }
        }
        mapper.updateLocation(dto, location);
        return mapper.toFullDto(repository.save(location));
    }

    public void deleteLocation(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<LocationFullDto> findLocations(int from, int size, Boolean onlyConfirmed, String name) {
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.DESC, "createdOn"));
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

    private Map<String, Float> coordinatesToUpdate(Float lon, Float lat, Location location) {
        Map<String, Float> map = new HashMap<>(2);
        if (lon != null && !lon.equals(location.getLon())) {
            map.put("lon", lon);
        }
        if (lat != null && !lat.equals(location.getLat())) {
            map.put("lat", lat);
        }
        return map;
    }
}
