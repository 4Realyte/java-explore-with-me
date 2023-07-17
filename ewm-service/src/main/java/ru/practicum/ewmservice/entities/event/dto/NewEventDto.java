package ru.practicum.ewmservice.entities.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewmservice.entities.event.model.Location;
import ru.practicum.ewmservice.entities.event.utils.EventDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class NewEventDto {
    @Size(min = 20, max = 2000)
    @NotBlank
    private String annotation;
    @JsonProperty("category")
    @NotBlank
    private Long categoryId;
    @Size(min = 20, max = 7000)
    @NotBlank
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    @EventDate
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    @Builder.Default
    private Boolean paid = false;
    @Builder.Default
    private Integer participantLimit = 0;
    @Builder.Default
    private Boolean requestModeration = true;
    @Size(min = 3, max = 120)
    @NotBlank
    private String title;
}
