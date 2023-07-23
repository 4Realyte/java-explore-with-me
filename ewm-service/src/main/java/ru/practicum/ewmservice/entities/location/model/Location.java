package ru.practicum.ewmservice.entities.location.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lat")
    private Float lat;
    @Column(name = "lon")
    private Float lon;
    @Column(name = "rad")
    private Float rad;
    @Column(name = "location_name")
    private String locationName;
    @Enumerated(EnumType.STRING)
    private LocationState state;
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @PrePersist
    public void setUpCreationDate() {
        createdOn = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
