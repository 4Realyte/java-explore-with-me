package ru.practicum.ewmservice.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.entities.compilation.dto.CompilationRequestDto;
import ru.practicum.ewmservice.entities.compilation.dto.CompilationResponseDto;
import ru.practicum.ewmservice.entities.compilation.service.CompilationServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Validated
public class AdminCompilationController {
    private final CompilationServiceImpl service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto addCompilation(@RequestBody @Validated(CompilationRequestDto.NewRequest.class) CompilationRequestDto dto) {
        return service.addCompilation(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") Long compId) {
        service.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationResponseDto updateCompilation(@PathVariable("compId") Long compId,
                                                    @RequestBody @Validated(CompilationRequestDto.UpdateRequest.class) CompilationRequestDto dto) {
        return service.updateCompilation(compId, dto);
    }
}
