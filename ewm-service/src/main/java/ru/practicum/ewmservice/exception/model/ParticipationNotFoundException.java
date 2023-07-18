package ru.practicum.ewmservice.exception.model;

public class ParticipationNotFoundException extends RuntimeException {
    public ParticipationNotFoundException(String message) {
        super(message);
    }
}
