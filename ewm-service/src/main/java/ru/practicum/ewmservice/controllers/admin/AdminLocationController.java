package ru.practicum.ewmservice.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.entities.location.dto.LocationFullDto;
import ru.practicum.ewmservice.entities.location.dto.LocationUpdateRequestDto;
import ru.practicum.ewmservice.entities.location.dto.NewLocationDto;
import ru.practicum.ewmservice.entities.location.service.LocationServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/locations")
public class AdminLocationController {
    private final LocationServiceImpl service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationFullDto addLocation(@RequestBody @Valid NewLocationDto dto) {
        return service.addLocationByAdmin(dto);
    }

    @PatchMapping("/{locId}")
    public LocationFullDto updateLocation(@RequestBody @Valid LocationUpdateRequestDto dto, @PathVariable Long locId) {
        return service.updateLocationByAdmin(dto, locId);
    }

    @GetMapping
    public List<LocationFullDto> findLocations(@RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(required = false) Boolean onlyConfirmed,
                                               @RequestParam(required = false) String name) {
        return service.findLocations(from, size, onlyConfirmed, name);
    }

    @DeleteMapping("/{locId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable("locId") Long locId) {
        service.deleteLocation(locId);
    }
}
