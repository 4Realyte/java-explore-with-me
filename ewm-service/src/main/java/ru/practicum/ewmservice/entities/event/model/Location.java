package ru.practicum.ewmservice.entities.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Location {
    @Column(name = "lat")
    private float lat;
    @Column(name = "lon")
    private float lon;
}
