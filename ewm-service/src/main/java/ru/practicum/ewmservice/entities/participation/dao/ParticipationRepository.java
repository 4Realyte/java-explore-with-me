package ru.practicum.ewmservice.entities.participation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewmservice.entities.participation.model.Participation;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    Optional<Participation> findByIdAndRequesterId(Long requestId, Long requestorId);

    @Query(value = "SELECT p FROM Participation as p " +
            "where p.requester.id = :id AND p.event.initiator.id != :id")
    List<Participation> findAllUserRequests(@Param("id") Long userId);

    List<Participation> findAllByEventId(Long eventId);

    List<Participation> findAllByIdIn(List<Long> ids);
}
