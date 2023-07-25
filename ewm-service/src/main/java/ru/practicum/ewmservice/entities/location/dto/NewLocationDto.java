package ru.practicum.ewmservice.entities.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class NewLocationDto {
    @NotNull
    private Float lat;
    @NotNull
    private Float lon;
    @NotNull
    private Float rad;
    @NotBlank
    @Size(min = 3, max = 30)
    private String locationName;
}
