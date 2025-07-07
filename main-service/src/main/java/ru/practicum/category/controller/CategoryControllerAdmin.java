package ru.practicum.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryControllerAdmin {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Получен запрос POST: /admin/categories на добавление категории {}.", newCategoryDto);
        CategoryDto categoryDto = categoryService.addCategory(newCategoryDto);
        log.info("Категория {} успешно добавлена в систему.", categoryDto);
        return categoryDto;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Получен запрос DELETE: /admin/categories{}", catId);
        categoryService.deleteCategory(catId);
        log.info("Категория с id: {} успешно удалена из системы.", catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Получен запрос PATCH: /admin/categories/{}, {} ", catId, newCategoryDto);
        CategoryDto categoryDto = categoryService.updateCategory(newCategoryDto, catId);
        log.info("Категория {} успешно обновлена в системы.", categoryDto);
        return categoryDto;
    }

}
