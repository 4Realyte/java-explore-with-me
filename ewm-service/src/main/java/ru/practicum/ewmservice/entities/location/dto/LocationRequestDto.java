package ru.practicum.ewmservice.entities.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewmservice.entities.location.utils.LocationRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
@LocationRequest
public class LocationRequestDto {
    private Float lon;
    private Float lat;
    private Float rad;
}
