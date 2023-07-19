package ru.practicum.ewmservice.exception.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewmservice.exception.model.*;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodEx(final MethodArgumentNotValidException ex) {
        List<ErrorMessage> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(ErrorMessage.builder()
                    .defaultMessage(error.getDefaultMessage())
                    .fieldName(error.getField())
                    .className(error.getObjectName())
                    .build());
        }
        return ApiError.builder()
                .errors(errors)
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler({BadRequestException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final RuntimeException ex) {
        List<ErrorMessage> errors = new ArrayList<>();
        for (StackTraceElement e : ex.getStackTrace()) {
            errors.add(ErrorMessage.builder()
                    .className(e.getClassName())
                    .methodName(e.getMethodName())
                    .build());
        }
        return ApiError.builder()
                .errors(errors)
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolationEx(final RuntimeException ex) {
        List<ErrorMessage> errors = new ArrayList<>();
        for (StackTraceElement e : ex.getStackTrace()) {
            errors.add(ErrorMessage.builder()
                    .className(e.getClassName())
                    .methodName(e.getMethodName())
                    .build());
        }
        return ApiError.builder()
                .errors(errors)
                .reason("For the requested operation the conditions are not met.")
                .status(HttpStatus.CONFLICT)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler({UserNotFoundException.class, CategoryNotFoundException.class,
            EventNotFoundException.class, ParticipationNotFoundException.class, CompilationNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundEx(final RuntimeException ex) {
        List<ErrorMessage> errors = new ArrayList<>();
        for (StackTraceElement e : ex.getStackTrace()) {
            errors.add(ErrorMessage.builder()
                    .className(e.getClassName())
                    .methodName(e.getMethodName())
                    .build());
        }
        return ApiError.builder()
                .errors(errors)
                .reason("Resource not found")
                .status(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .build();
    }
}
