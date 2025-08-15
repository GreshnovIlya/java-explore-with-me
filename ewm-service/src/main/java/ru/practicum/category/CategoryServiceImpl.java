package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.exception.NewBadRequestException;
import ru.practicum.exception.NewConstraintViolationException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto categoryDto) {
        if (categoryDto.getName() == null) {
            throw new NewBadRequestException("Field: name. Error: must not be blank. Value: null");
        } else if (categoryDto.getName().length() > 50 || categoryDto.getName().isBlank()) {
            throw new NewBadRequestException(String.format("Field: name. Error: it must be between 1 and 50 " +
                    "characters long. Long: %s", categoryDto.getName().length()));
        }
        if (!categoryRepository.findByName(categoryDto.getName()).isEmpty()) {
            throw new NewConstraintViolationException("could not execute statement; SQL [n/a]; constraint " +
                    "[uq_category_name]; nested exception is org.hibernate.exception.ConstraintViolationException: " +
                    "could not execute statement", "CONFLICT", "Integrity constraint has been violated.");
        }
        Category category = categoryRepository.save(CategoryMapper.toCategory(categoryDto));
        log.info("Админ создает новую категорию: {}", category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void removeCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException(String.format("Category with id=%s was not found", catId)));
        if (eventRepository.findByCategory(catId).isEmpty()) {
            categoryRepository.delete(category);
            log.info("Админ удалил категорию: {}", category);
        } else {
            throw new NewConstraintViolationException("The category is not empty ", "CONFLICT",
                    "For the requested operation the conditions are not met.");
        }
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException(String.format("Category with id=%s was not found", catId)));
        if (categoryDto.getName() != null) {
            if (categoryDto.getName().length() > 50 || categoryDto.getName().isBlank()) {
                throw new NewBadRequestException(String.format("Field: name. Error: it must be between 1 and 50 " +
                        "characters long. Long: %s", categoryDto.getName().length()));
            }
            List<Category> categories = categoryRepository.findByName(categoryDto.getName());
            if (categories.isEmpty() || (categories.size() == 1 &&
                    categories.getFirst().getName().equals(category.getName()))) {
                category.setName(categoryDto.getName());
            } else {
                throw new NewConstraintViolationException("could not execute statement; SQL [n/a]; constraint " +
                        "[uq_category_name]; nested exception is org.hibernate.exception.ConstraintViolationException: " +
                        "could not execute statement", "CONFLICT", "Integrity constraint has been violated.");
            }
        }
        category = categoryRepository.save(category);
        log.info("Админ обновил категорию: {}", category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        List<Category> categories = categoryRepository.findByFromAndLimit(from, size);
        log.info("Получены категории: {}", categories);
        return categories.stream().map(CategoryMapper::toCategoryDto).toList();
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException(String.format("Category with id=%s was not found", catId)));
        log.info("Получена категория: {}", category);
        return CategoryMapper.toCategoryDto(category);
    }
}