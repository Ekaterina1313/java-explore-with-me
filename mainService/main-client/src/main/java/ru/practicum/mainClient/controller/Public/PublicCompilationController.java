package ru.practicum.mainClient.controller.Public;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainClient.client.Public.PublicCompilationClient;

@RestController
@Slf4j
@RequestMapping("/compilations")
public class PublicCompilationController {
    private final PublicCompilationClient client;

    public PublicCompilationController(PublicCompilationClient client) {
        this.client = client;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAll(@RequestParam(name = "pinned", defaultValue = "false") Boolean pinned,
                                         @RequestParam(name = "from", defaultValue = "0") int from,
                                         @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("PUBLIC-controller: Поступил запрос на просмотр подборок c pinned = : " + pinned);
        try {
            return client.getAll(pinned, from, size);
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        }

    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getById(@PathVariable Integer compId) {
        log.info("PUBLIC-controller: Поступил запрос на просмотр подборки c pinned = : " + compId);
        try {
            return client.getById(compId);
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        }
    }
}