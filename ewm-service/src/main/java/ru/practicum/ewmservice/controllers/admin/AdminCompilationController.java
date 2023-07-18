package ru.practicum.ewmservice.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.entities.compilation.dto.CompilationRequestDto;
import ru.practicum.ewmservice.entities.compilation.dto.CompilationResponseDto;
import ru.practicum.ewmservice.entities.compilation.service.CompilationServiceImpl;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final CompilationServiceImpl service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto addCompilation(@RequestBody @Valid CompilationRequestDto dto) {
        return service.addCompilation(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") Long compId) {
        service.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationResponseDto updateCompilation(@PathVariable("compId") Long compId,
                                                    @RequestBody @Valid CompilationRequestDto dto) {
        return service.updateCompilation(compId, dto);
    }
}
