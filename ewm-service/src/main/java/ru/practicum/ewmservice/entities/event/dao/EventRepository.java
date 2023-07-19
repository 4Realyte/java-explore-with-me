package ru.practicum.ewmservice.entities.event.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewmservice.entities.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    @Query("SELECT ev FROM Event as ev " +
            "WHERE ev.id = ?1 AND ev.state = 'PUBLISHED'")
    Optional<Event> findPublishedEvent(Long eventId);

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable page);
}
