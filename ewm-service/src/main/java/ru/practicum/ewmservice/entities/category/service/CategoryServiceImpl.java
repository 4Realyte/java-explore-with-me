package ru.practicum.ewmservice.entities.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.entities.category.dao.CategoryRepository;
import ru.practicum.ewmservice.entities.category.dto.CategoryRequestDto;
import ru.practicum.ewmservice.entities.category.dto.CategoryResponseDto;
import ru.practicum.ewmservice.entities.category.mapper.CategoryMapper;
import ru.practicum.ewmservice.entities.category.model.Category;
import ru.practicum.ewmservice.exception.CategoryNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Transactional
    public CategoryResponseDto addCategory(CategoryRequestDto dto) {
        Category category = mapper.dtoToCategory(dto);
        return mapper.toDto(repository.save(category));
    }

    @Transactional
    public CategoryResponseDto updateCategory(Long catId, CategoryRequestDto dto) {
        Category category = repository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id %s not found", catId)));
        category.setName(dto.getName());
        return mapper.toDto(repository.save(category));
    }

    @Transactional
    public void deleteCategoryById(Long catId) {
        repository.deleteById(catId);
    }

    public List<CategoryResponseDto> getCategories(int from, int size) {
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.ASC, "id"));
        return mapper.toDto(repository.findAll(page).getContent());
    }

    public CategoryResponseDto getCategoryById(Long catId) {
        return mapper.toDto(repository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id %s not found", catId))));
    }
}
