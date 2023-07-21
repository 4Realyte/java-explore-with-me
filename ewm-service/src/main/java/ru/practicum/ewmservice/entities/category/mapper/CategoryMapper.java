package ru.practicum.ewmservice.entities.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewmservice.entities.category.dto.CategoryRequestDto;
import ru.practicum.ewmservice.entities.category.dto.CategoryResponseDto;
import ru.practicum.ewmservice.entities.category.model.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category dtoToCategory(CategoryRequestDto dto);

    CategoryResponseDto toDto(Category category);

    List<CategoryResponseDto> toDto(List<Category> categories);
}
