package ru.practicum.ewmservice.entities.event.utils;

import ru.practicum.ewmservice.entities.event.dto.NewEventDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<EventDate, NewEventDto> {
    @Override
    public boolean isValid(NewEventDto newEventDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime future = LocalDateTime.now().plusHours(2L);
        if (newEventDto.getEventDate().isBefore(future)) {
            return false;
        }
        return true;
    }
}
