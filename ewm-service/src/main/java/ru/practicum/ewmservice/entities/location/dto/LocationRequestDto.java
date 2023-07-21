package ru.practicum.ewmservice.entities.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class LocationRequestDto {
    private Float lon;
    private Float lat;
    private Float rad;
    private boolean isRequired;
    private String locationName;

    public void checkRequired() {
        if (lat == null && lon == null && rad == null) {
            this.isRequired = false;
        } else {
            this.isRequired = true;
        }
    }
}
