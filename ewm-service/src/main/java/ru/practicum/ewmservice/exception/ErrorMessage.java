package ru.practicum.ewmservice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Jacksonized
public class ErrorMessage {
    private String methodName;
    private String className;
    @Builder.Default
    private String fieldName = "UNDEFINED";
    @Builder.Default
    private String defaultMessage = "UNDEFINED";
}
