package ru.practicum.ewmservice.exception;

public class ParticipationNotFoundException extends RuntimeException {
    public ParticipationNotFoundException(String message) {
        super(message);
    }
}
