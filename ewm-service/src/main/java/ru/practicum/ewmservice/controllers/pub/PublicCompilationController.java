package ru.practicum.ewmservice.controllers.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.entities.compilation.dto.CompilationResponseDto;
import ru.practicum.ewmservice.entities.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {
    private final CompilationService service;

    @GetMapping
    public List<CompilationResponseDto> findCompilations(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                                         @RequestParam(defaultValue = "0") int from,
                                                         @RequestParam(defaultValue = "10") int size) {
        return service.findCompillations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationResponseDto findCompilation(@PathVariable Long compId) {
        return service.findById(compId);
    }
}
