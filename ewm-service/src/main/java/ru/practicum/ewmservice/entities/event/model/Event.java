package ru.practicum.ewmservice.entities.event.model;

import lombok.*;
import ru.practicum.ewmservice.entities.category.model.Category;
import ru.practicum.ewmservice.entities.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @Embedded
    private Location location;
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    private String title;
    @ManyToOne
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
        createdOn = LocalDateTime.now();
    }

    @PreUpdate
    public void setUpPublishingDate() {
        if (state.equals(EventState.PUBLISHED)) {
            publishedOn = LocalDateTime.now();
        }
    }
}
