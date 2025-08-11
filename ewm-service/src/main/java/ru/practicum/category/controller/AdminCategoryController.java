package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.CategoryService;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private CategoryDto createCategory(@RequestBody NewCategoryDto categoryDto) {
        return service.createCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void removeCategory(@PathVariable Long catId) {
        service.removeCategory(catId);
    }

    @PatchMapping("/{catId}")
    private CategoryDto updateCategory(@PathVariable Long catId, @RequestBody NewCategoryDto categoryDto) {
        return service.updateCategory(catId, categoryDto);
    }
}
