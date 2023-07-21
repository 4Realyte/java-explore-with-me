package ru.practicum.ewmservice.entities.event.utils;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {EventDateValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventDate {
    String message() default "Дата события не может быть ранее, чем через 2 часа";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
