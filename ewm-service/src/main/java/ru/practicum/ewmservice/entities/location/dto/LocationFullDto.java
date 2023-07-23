package ru.practicum.ewmservice.entities.location.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewmservice.entities.location.model.LocationState;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class LocationFullDto {
    private Long id;
    private Float lat;
    private Float lon;
    private Float rad;
    private String locationName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String address;
    private LocationState state;
}
