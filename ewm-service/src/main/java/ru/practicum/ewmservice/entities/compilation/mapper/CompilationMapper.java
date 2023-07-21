package ru.practicum.ewmservice.entities.compilation.mapper;

import org.mapstruct.*;
import ru.practicum.ewmservice.entities.compilation.dto.CompilationRequestDto;
import ru.practicum.ewmservice.entities.compilation.dto.CompilationResponseDto;
import ru.practicum.ewmservice.entities.compilation.model.Compilation;
import ru.practicum.ewmservice.entities.event.mapper.EventMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface CompilationMapper {
    @Mapping(target = "events", ignore = true)
    Compilation dtoToCompilation(CompilationRequestDto dto);

    CompilationResponseDto toResponseDto(Compilation compilation);

    List<CompilationResponseDto> toResponseDto(List<Compilation> compilations);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "events", ignore = true)
    void updateCompilation(CompilationRequestDto dto, @MappingTarget Compilation compilation);
}
