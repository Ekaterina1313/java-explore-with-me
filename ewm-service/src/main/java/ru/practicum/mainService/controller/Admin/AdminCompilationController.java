package ru.practicum.mainService.controller.Admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.dto.CompilationDto;
import ru.practicum.mainService.dto.NewCompilationDto;
import ru.practicum.mainService.error.InvalidRequestException;
import ru.practicum.mainService.service.Admin.AdminCompilationService;

@RestController
@Slf4j
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final AdminCompilationService compilationService;

    public AdminCompilationController(AdminCompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@RequestBody NewCompilationDto newCompilationDto) {
        log.info("ADMIN-controller: Поступил запрос на добавление новой подборки: {}", newCompilationDto.getTitle());
        validTitle(newCompilationDto);
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        return compilationService.create(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer compId) {
        log.info("ADMIN-controller: Поступил запрос на удаление подборки событий с id = {}", compId);
        compilationService.deleteById(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto update(@PathVariable Integer compId, @RequestBody NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getTitle() != null) {
            validTitle(newCompilationDto);
        }
        return compilationService.update(compId, newCompilationDto);
    }

    private void validTitle(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getTitle() == null || newCompilationDto.getTitle().isBlank() ||
                newCompilationDto.getTitle().length() > 50) {
            throw new InvalidRequestException("Field: title. Error: must not be blank or less than 50 symbols." +
                    " Value: " + newCompilationDto.getTitle());
        }
    }
}