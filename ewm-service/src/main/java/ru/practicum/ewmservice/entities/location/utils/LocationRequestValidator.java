package ru.practicum.ewmservice.entities.location.utils;

import ru.practicum.ewmservice.entities.location.dto.LocationRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public class LocationRequestValidator implements ConstraintValidator<LocationRequest, LocationRequestDto> {
    @Override
    public boolean isValid(LocationRequestDto dto, ConstraintValidatorContext constraintValidatorContext) {
        Field[] declaredFields = dto.getClass().getDeclaredFields();
        long count = Arrays.stream(declaredFields)
                .peek(d -> d.setAccessible(true))
                .map(d -> getFieldValue(d, dto))
                .filter(Objects::isNull)
                .count();
        if (count > 0 && count != declaredFields.length) {
            return false;
        }
        return true;
    }

    private static Object getFieldValue(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
