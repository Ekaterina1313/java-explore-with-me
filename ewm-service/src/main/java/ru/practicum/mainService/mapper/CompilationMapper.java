package ru.practicum.mainService.mapper;

import ru.practicum.mainService.dto.CompilationDto;
import ru.practicum.mainService.dto.EventShortDto;
import ru.practicum.mainService.dto.NewCompilationDto;
import ru.practicum.mainService.model.Compilation;

import java.util.List;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return new Compilation(
                newCompilationDto.getId(),
                newCompilationDto.getPinned(),
                newCompilationDto.getTitle()
        );
    }

    public static CompilationDto compilationDto(Compilation compilation, List<EventShortDto> events) {
        return new CompilationDto(
                events,
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }
}