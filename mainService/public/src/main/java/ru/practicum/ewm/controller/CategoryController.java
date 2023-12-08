package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.CategoryDto;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(name = "from", defaultValue = "0") int from,
                                    @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("PUBLIC-controller: Поступил запрос на просмотр всех категорий.");
        try {
            return categoryService.getAll(from, size);
        } catch (DataIntegrityViolationException ex) {
            log.error("PUBLIC-controller: Error getting categories", ex);
            throw ex;
        }
    }

    @GetMapping("/{catId}")
    public CategoryDto getById(@PathVariable Integer catId) {
        log.info("PUBLIC-controller: Поступил запрос на просмотр категории с id = ." + catId);
        try {
            return categoryService.getById(catId);
        } catch (DataIntegrityViolationException ex) {
            log.error("PUBLIC-controller: Error getting category", ex);
            throw ex;
        }
    }
}