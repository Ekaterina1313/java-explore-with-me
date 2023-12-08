package ru.practicum.mainClient.controller.Admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.NewCompilationDto;
import ru.practicum.common.error.InvalidRequestException;
import ru.practicum.mainClient.client.Admin.AdminCompilationClient;

@RestController
@Slf4j
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final AdminCompilationClient client;

    public AdminCompilationController(AdminCompilationClient client) {
        this.client = client;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody NewCompilationDto newCompilationDto) {
        log.info("ADMIN-controller: Поступил запрос на добавление новой подборки: " + newCompilationDto.getTitle());
        validTitle(newCompilationDto);
        try {
            return client.create(newCompilationDto);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error creating compilation", ex);
            throw ex;
        }
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteById(@PathVariable Integer compId) {
        log.info("ADMIN-controller: Поступил запрос на удаление подборки событий с id = " + compId);
        return client.deleteById(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@PathVariable Integer compId, @RequestBody NewCompilationDto newCompilationDto) {
        return client.update(compId, newCompilationDto);
    }

    private void validTitle(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getTitle() == null || newCompilationDto.getTitle().isBlank()) {
            throw new InvalidRequestException("Field: title. Error: must not be blank. Value: null");
        }
    }
}