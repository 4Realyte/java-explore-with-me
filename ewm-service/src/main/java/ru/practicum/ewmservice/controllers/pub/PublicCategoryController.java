package ru.practicum.ewmservice.controllers.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.entities.category.dto.CategoryResponseDto;
import ru.practicum.ewmservice.entities.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
public class PublicCategoryController {
    private final CategoryService service;

    @GetMapping
    public List<CategoryResponseDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        return service.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryResponseDto getCategory(@PathVariable("catId") Long catId) {
        return service.getCategoryById(catId);
    }
}
