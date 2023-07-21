package ru.practicum.ewmservice.entities.event.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.entities.event.dto.EventStateAction;

@Component
public class EventStateActionConverter implements Converter<String, EventStateAction> {
    @Override
    public EventStateAction convert(String source) {
        try {
            return EventStateAction.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
