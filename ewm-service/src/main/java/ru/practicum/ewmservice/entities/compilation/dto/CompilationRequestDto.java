package ru.practicum.ewmservice.entities.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class CompilationRequestDto {
    public interface NewRequest {
    }

    public interface UpdateRequest {
    }

    private List<Long> events;
    @Builder.Default
    private Boolean pinned = false;
    @Size(min = 1, max = 50, groups = {NewRequest.class, UpdateRequest.class})
    @NotBlank(groups = NewRequest.class)
    private String title;
}
