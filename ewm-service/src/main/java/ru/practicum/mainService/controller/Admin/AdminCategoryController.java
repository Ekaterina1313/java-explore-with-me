package ru.practicum.mainService.controller.Admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.dto.CategoryDto;
import ru.practicum.mainService.error.InvalidRequestException;
import ru.practicum.mainService.service.Admin.AdminCategoryService;

@RestController
@Slf4j
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final AdminCategoryService categoryService;

    public AdminCategoryController(AdminCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody CategoryDto categoryDto) {
        log.info("ADMIN-controller: Поступил запрос на добавление новой категории события = {}", categoryDto.getName());
        validCategoryName(categoryDto);
        return categoryService.create(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer catId) {
        log.info("Поступил запрос на удаление категории с id = {}.", catId);
        categoryService.delete(catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@PathVariable Integer catId, @RequestBody CategoryDto categoryDto) {
        log.info("ADMIN-controller: Поступил запрос на обновление категории события = {}", catId);
        validCategoryName(categoryDto);
        return categoryService.update(catId, categoryDto);
    }

    private void validCategoryName(CategoryDto categoryDto) {
        if (categoryDto.getName() == null || categoryDto.getName().isBlank() ||
                categoryDto.getName().length() > 50) {
            throw new InvalidRequestException("Field: name. Error: must not be blank or blank or length > 50. " +
                    "Value: " + categoryDto.getName());
        }
    }
}