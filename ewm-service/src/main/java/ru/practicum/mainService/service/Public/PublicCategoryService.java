package ru.practicum.mainService.service.Public;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainService.dto.CategoryDto;
import ru.practicum.mainService.mapper.CategoryMapper;
import ru.practicum.mainService.model.Category;
import ru.practicum.mainService.repository.CategoryRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicCategoryService {
    private final CategoryRepository categoryRepository;

    public PublicCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        return categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getById(Integer catId) {
        Category categoryById = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id=" + catId + " was not found"));
        return CategoryMapper.toCategoryDto(categoryById);
    }
}