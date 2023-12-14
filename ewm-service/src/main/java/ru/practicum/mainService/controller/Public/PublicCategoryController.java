package ru.practicum.mainService.controller.Public;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.dto.CategoryDto;
import ru.practicum.mainService.service.Public.PublicCategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Slf4j
public class PublicCategoryController {
    private final PublicCategoryService categoryService;

    public PublicCategoryController(PublicCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(name = "from", defaultValue = "0") int from,
                                    @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("PUBLIC-controller: Поступил запрос на просмотр всех категорий.");
        return categoryService.getAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getById(@PathVariable Integer catId) {
        log.info("PUBLIC-controller: Поступил запрос на просмотр категории с id = {}", catId);
        return categoryService.getById(catId);
    }
}