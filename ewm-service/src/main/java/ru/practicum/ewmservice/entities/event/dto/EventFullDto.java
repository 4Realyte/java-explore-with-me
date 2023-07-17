package ru.practicum.ewmservice.entities.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmservice.entities.category.dto.CategoryResponseDto;
import ru.practicum.ewmservice.entities.event.model.EventState;
import ru.practicum.ewmservice.entities.event.model.Location;
import ru.practicum.ewmservice.entities.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private Long id;
    private String annotation;
    private CategoryResponseDto category;
    private Integer confirmedRequests;
    private LocalDateTime createdOn;
    private String description;
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventState state;
    private String title;
    @Builder.Default
    private Integer views = 0;
}
