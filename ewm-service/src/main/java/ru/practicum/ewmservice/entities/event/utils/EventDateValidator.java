package ru.practicum.ewmservice.entities.event.utils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<EventDate, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime future = LocalDateTime.now().plusHours(2L);
        if (localDateTime != null && localDateTime.isBefore(future)) {
            return false;
        }
        return true;
    }
}
