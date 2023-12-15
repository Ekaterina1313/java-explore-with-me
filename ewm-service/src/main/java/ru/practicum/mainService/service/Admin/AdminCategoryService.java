package ru.practicum.mainService.service.Admin;

import org.springframework.stereotype.Service;
import ru.practicum.mainService.dto.CategoryDto;
import ru.practicum.mainService.mapper.CategoryMapper;
import ru.practicum.mainService.model.Category;
import ru.practicum.mainService.repository.CategoryRepository;
import ru.practicum.mainService.service.ValidationById;

@Service
public class AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final ValidationById validationById;

    public AdminCategoryService(CategoryRepository categoryRepository, ValidationById validationById) {
        this.categoryRepository = categoryRepository;
        this.validationById = validationById;
    }

    public CategoryDto create(CategoryDto categoryDto) {
        Category createdCategory = categoryRepository.save(CategoryMapper.fromCategoryDto(categoryDto));
        return CategoryMapper.toCategoryDto(createdCategory);
    }

    public void delete(Integer categoryId) {
        validationById.getCategoryById(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    public CategoryDto update(Integer catId, CategoryDto categoryDto) {
        Category categoryById = validationById.getCategoryById(catId);
        if (categoryById.getName().equals(categoryDto.getName())) {
            return CategoryMapper.toCategoryDto(categoryById);
        }
        categoryById.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(categoryById);
        return CategoryMapper.toCategoryDto(updatedCategory);
    }
}