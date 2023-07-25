package ru.practicum.ewmservice.entities.location.utils;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = { LocationRequestValidator.class })
@Documented
public @interface LocationRequest {
    String message() default "Location request must contain lon, lat and rad attributes";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
