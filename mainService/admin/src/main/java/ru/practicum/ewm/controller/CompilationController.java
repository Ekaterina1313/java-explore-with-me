package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.CompilationDto;
import ru.practicum.common.dto.NewCompilationDto;
import ru.practicum.ewm.service.CompilationService;

@RestController
@Slf4j
@RequestMapping("/admin/compilations")
public class CompilationController {

    private final CompilationService compilationService;

    public CompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@RequestBody NewCompilationDto newCompilationDto) {
        log.info("ADMIN-controller: Поступил запрос на добавление новой подборки: " + newCompilationDto.getTitle());
        try {
            return compilationService.create(newCompilationDto);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error creating compilation", ex);
            throw ex;
        }
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer compId) {
        log.info("ADMIN-controller: Поступил запрос на удаление подборки событий с id = " + compId);
        compilationService.deleteById(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto update(@PathVariable Integer compId, @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.update(compId, newCompilationDto);
    }
}