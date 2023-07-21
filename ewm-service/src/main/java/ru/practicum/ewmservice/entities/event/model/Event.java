package ru.practicum.ewmservice.entities.event.model;

import lombok.*;
import org.hibernate.annotations.Formula;
import ru.practicum.ewmservice.entities.category.model.Category;
import ru.practicum.ewmservice.entities.location.model.Location;
import ru.practicum.ewmservice.entities.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "confirmed_requests")
    @Formula(value = "(SELECT COALESCE(COUNT(R.id),0) FROM REQUESTS AS R WHERE R.event_id = ID AND R.status = 'CONFIRMED')")
    private Integer confirmedRequests;
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    @ToString.Exclude
    private Location location;
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    private String title;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    @ToString.Exclude
    private User initiator;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EventState state = EventState.PENDING;

    @PrePersist
    public void setUpCreationDate() {
        createdOn = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    @PreUpdate
    public void setUpPublishingDate() {
        if (state.equals(EventState.PUBLISHED)) {
            publishedOn = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        }
    }
}
