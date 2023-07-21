package ru.practicum.ewmservice.entities.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewmservice.entities.category.dto.CategoryResponseDto;
import ru.practicum.ewmservice.entities.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class EventShortDto {
    private Long id;
    private String annotation;
    private CategoryResponseDto category;
    private Integer confirmedRequests;
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    @Builder.Default
    private Integer views = 0;
}
