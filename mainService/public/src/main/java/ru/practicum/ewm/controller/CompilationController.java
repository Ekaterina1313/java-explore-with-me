package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.CompilationDto;
import ru.practicum.ewm.service.CompilationService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/compilations")
public class CompilationController {

    private final CompilationService compilationService;

    public CompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getAll(@RequestParam(name = "pinned", defaultValue = "false") Boolean pinned,
                                       @RequestParam(name = "from", defaultValue = "0") int from,
                                       @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("PUBLIC-controller: Поступил запрос на просмотр подборок c pinned = : " + pinned);
        try {
            return compilationService.getAll(pinned, from, size);
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        }
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getById(@PathVariable Integer compId) {
        log.info("PUBLIC-controller: Поступил запрос на просмотр подборки c pinned = : " + compId);
        try {
            return compilationService.getById(compId);
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        }
    }
}