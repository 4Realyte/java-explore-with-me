package ru.practicum.ewmservice.entities.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.entities.compilation.dao.CompilationRepository;
import ru.practicum.ewmservice.entities.compilation.dto.CompilationRequestDto;
import ru.practicum.ewmservice.entities.compilation.dto.CompilationResponseDto;
import ru.practicum.ewmservice.entities.compilation.mapper.CompilationMapper;
import ru.practicum.ewmservice.entities.compilation.model.Compilation;
import ru.practicum.ewmservice.entities.event.dao.EventRepository;
import ru.practicum.ewmservice.entities.event.model.Event;
import ru.practicum.ewmservice.exception.model.CompilationNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl {
    private final CompilationRepository repository;
    private final EventRepository eventRepository;
    private final CompilationMapper mapper;

    @Transactional
    public CompilationResponseDto addCompilation(CompilationRequestDto dto) {
        List<Event> events = null;
        if (dto.getEvents() != null) {
            events = eventRepository.findAllById(dto.getEvents());
        }
        Compilation compilation = mapper.dtoToCompilation(dto);
        compilation.setEvents(events);
        return mapper.toResponseDto(repository.save(compilation));
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        try {
            repository.deleteById(compId);
        } catch (EmptyResultDataAccessException e) {
            throw new CompilationNotFoundException(String.format("Compilation with id %s doesn't exist", compId));
        }
    }

    @Transactional
    public CompilationResponseDto updateCompilation(Long compId, CompilationRequestDto dto) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(String.format("Compilation with id %s doesn't exist", compId)));
        List<Event> events = null;
        if (dto.getEvents() != null) {
            events = eventRepository.findAllById(dto.getEvents());
        }
        compilation.setEvents(events);
        mapper.updateCompilation(dto, compilation);
        return mapper.toResponseDto(repository.save(compilation));
    }

    public List<CompilationResponseDto> findCompillations(Boolean pinned, int from, int size) {
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        if (pinned != null) {
            return mapper.toResponseDto(repository.findAllByPinned(pinned, page));
        } else {
            return mapper.toResponseDto(repository.findAll(page).getContent());
        }
    }

    public CompilationResponseDto findById(Long compId) {
        return mapper.toResponseDto(repository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(String.format("Compilation with id %s doesn't exist", compId))));
    }
}
