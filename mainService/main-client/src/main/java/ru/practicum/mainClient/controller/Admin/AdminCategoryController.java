package ru.practicum.mainClient.controller.Admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.CategoryDto;
import ru.practicum.common.error.InvalidRequestException;
import ru.practicum.mainClient.client.Admin.AdminCategoryClient;

@RestController
@Slf4j
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final AdminCategoryClient client;

    public AdminCategoryController(AdminCategoryClient client) {
        this.client = client;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody CategoryDto categoryDto) {
        log.info("ADMIN-controller: Поступил запрос на добавление новой категории события = " + categoryDto.getName());
        validName(categoryDto);
        try {
            return client.create(categoryDto);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error creating user", ex);
            throw ex;
        }
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@PathVariable Integer catId, @RequestBody CategoryDto categoryDto) {
        log.info("ADMIN-controller: Поступил запрос на обновление категории события = " + catId);
        validName(categoryDto);
        try {
            return client.update(catId, categoryDto);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error updating category", ex);
            throw ex;
        }
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> delete(@PathVariable Integer catId) {
        log.info("Поступил запрос на удаление категории с id = {}.", catId);
        return client.delete(catId);
    }

    private void validName(CategoryDto categoryDto) {
        if (categoryDto.getName() == null || categoryDto.getName().isBlank()) {
            throw new InvalidRequestException("Field: name. Error: must not be blank. Value: null");
        }
    }
}