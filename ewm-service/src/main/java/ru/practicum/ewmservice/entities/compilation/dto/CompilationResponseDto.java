package ru.practicum.ewmservice.entities.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewmservice.entities.event.dto.EventShortDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class CompilationResponseDto {
    private Long id;
    private List<EventShortDto> events;
    private Boolean pinned;
    private String title;
}
