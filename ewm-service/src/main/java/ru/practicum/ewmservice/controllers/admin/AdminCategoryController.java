package ru.practicum.ewmservice.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.entities.category.dto.CategoryRequestDto;
import ru.practicum.ewmservice.entities.category.dto.CategoryResponseDto;
import ru.practicum.ewmservice.entities.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto addCategory(@Valid @RequestBody CategoryRequestDto dto) {
        return service.addCategory(dto);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponseDto updateCategory(@PathVariable("catId") Long catId,
                                              @Valid @RequestBody CategoryRequestDto dto) {
        return service.updateCategory(catId, dto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Long catId) {
        service.deleteCategoryById(catId);
    }
}
