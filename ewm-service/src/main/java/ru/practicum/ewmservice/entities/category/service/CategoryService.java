package ru.practicum.ewmservice.entities.category.service;

import ru.practicum.ewmservice.entities.category.dto.CategoryRequestDto;
import ru.practicum.ewmservice.entities.category.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto addCategory(CategoryRequestDto dto);

    CategoryResponseDto updateCategory(Long catId, CategoryRequestDto dto);

    void deleteCategoryById(Long catId);

    List<CategoryResponseDto> getCategories(int from, int size);

    CategoryResponseDto getCategoryById(Long catId);
}
