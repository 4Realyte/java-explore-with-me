package ru.practicum.ewmservice.entities.event.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.entities.event.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
