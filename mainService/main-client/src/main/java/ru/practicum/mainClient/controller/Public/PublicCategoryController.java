package ru.practicum.mainClient.controller.Public;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainClient.client.Public.PublicCategoryClient;

@RestController
@RequestMapping("/categories")
@Slf4j
public class PublicCategoryController {
    private final PublicCategoryClient client;

    public PublicCategoryController(PublicCategoryClient client) {
        this.client = client;
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestParam(name = "from", defaultValue = "0") int from,
                                         @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("PUBLIC-controller: Поступил запрос на просмотр всех категорий.");
        try {
            return client.getAll(from, size);
        } catch (DataIntegrityViolationException ex) {
            log.error("PUBLIC-controller: Error getting categories", ex);
            throw ex;
        }
    }

    @GetMapping("/{catId}")
    public ResponseEntity<Object> getById(@PathVariable Integer catId) {
        log.info("PUBLIC-controller: Поступил запрос на просмотр категории с id = ." + catId);
        try {
            return client.getById(catId);
        } catch (DataIntegrityViolationException ex) {
            log.error("PUBLIC-controller: Error getting category", ex);
            throw ex;
        }
    }
}