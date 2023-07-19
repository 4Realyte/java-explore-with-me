package ru.practicum.ewmservice.entities.compilation.service;

import ru.practicum.ewmservice.entities.compilation.dto.CompilationRequestDto;
import ru.practicum.ewmservice.entities.compilation.dto.CompilationResponseDto;

import java.util.List;

public interface CompilationService {
    CompilationResponseDto addCompilation(CompilationRequestDto dto);

    void deleteCompilation(Long compId);

    CompilationResponseDto updateCompilation(Long compId, CompilationRequestDto dto);

    List<CompilationResponseDto> findCompillations(Boolean pinned, int from, int size);

    CompilationResponseDto findById(Long compId);
}
