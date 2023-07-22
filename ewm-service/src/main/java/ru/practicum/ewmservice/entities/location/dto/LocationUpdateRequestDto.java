package ru.practicum.ewmservice.entities.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class LocationUpdateRequestDto {
    private Float lat;
    private Float lon;
    private Float rad;
    @NotBlank
    @Size(min = 3, max = 30)
    private String locationName;
    private LocationStateAction state;
}
