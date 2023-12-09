package ru.practicum.mainService.service.Admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainService.dto.CategoryDto;
import ru.practicum.mainService.mapper.CategoryMapper;
import ru.practicum.mainService.model.Category;
import ru.practicum.mainService.repository.CategoryRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminCategoryService {
    private final CategoryRepository categoryRepository;

    public AdminCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryDto create(CategoryDto categoryDto) {
        Category createdCategory = categoryRepository.save(CategoryMapper.fromCategoryDto(categoryDto));
        return CategoryMapper.toCategoryDto(createdCategory);
    }

    public void delete(Integer categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id=" + categoryId + " was not found"));
        categoryRepository.deleteById(categoryId);
    }

    public CategoryDto update(Integer catId, CategoryDto categoryDto) {
        Category categoryById = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id=" + catId + " was not found"));
        if (categoryById.getName().equals(categoryDto.getName())) {
            return CategoryMapper.toCategoryDto(categoryById);
        }
        categoryById.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(categoryById);
        return CategoryMapper.toCategoryDto(updatedCategory);
    }

    public List<CategoryDto> getAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }
}