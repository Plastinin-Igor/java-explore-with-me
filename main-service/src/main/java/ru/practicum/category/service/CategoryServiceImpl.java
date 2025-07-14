package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConstraintViolationException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        if (getCatByName(newCategoryDto.getName()) != null) {
            log.error("Категория с именем {} уже существует в системе. Добавление недопустимо.", newCategoryDto.getName());
            throw new ConstraintViolationException("Категория с именем " + newCategoryDto.getName()
                                                   + " уже существует в системе. Добавление недопустимо.");
        }
        Category category = categoryRepository.save(CategoryMapper.toCategoryFromNewCategory(newCategoryDto));
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void deleteCategory(Long catId) {
        Category category = getCatById(catId);
        if (eventRepository.countByCategory_Id(catId) == 0) {
            categoryRepository.delete(category);
        } else {
            log.error("Категория привязана к событию. Удаление недопустимо.");
            throw new ConstraintViolationException("Категория привязана к событию. Удаление недопустимо.");
        }

    }

    @Override
    @Transactional
    public CategoryDto updateCategory(NewCategoryDto newCategoryDto, Long catId) {
        Category oldCategory = getCatById(catId);
        Category newCategory = CategoryMapper.toCategoryFromNewCategory(newCategoryDto);

        if (oldCategory.getName().equals(newCategoryDto.getName())) {
            return CategoryMapper.toCategoryDto(oldCategory);
        } else {
            if (getCatByName(newCategoryDto.getName()) == null) {
                return CategoryMapper.toCategoryDto(categoryRepository.save(newCategory));
            } else {
                log.error("Категория с именем {} уже существует в системе. Исправление недопустимо.", newCategory.getName());
                throw new ConstraintViolationException("Категория с именем " + newCategoryDto.getName()
                                                       + " уже существует в системе. Исправление недопустимо.");
            }
        }

    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        PageRequest page = PageRequest.of(from, size, Sort.by("id").ascending());
        return categoryRepository.findAll(page)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Категория с id: " + catId + " не найдена в системе")));
    }

    private Category getCatById(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Категория с id: " + catId + " не найдена в системе"));
    }

    private Category getCatByName(String catName) {
        return categoryRepository.findByName(catName);
    }
}
