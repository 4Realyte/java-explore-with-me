package ru.practicum.ewmservice.entities.participation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationUpdateRequest {
    List<Long> requestIds;
    ParticipationStatus status;
}
