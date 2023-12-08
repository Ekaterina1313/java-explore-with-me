package ru.practicum.common.mapper;

import ru.practicum.common.dto.CompilationDto;
import ru.practicum.common.dto.EventShortDto;
import ru.practicum.common.dto.NewCompilationDto;
import ru.practicum.common.model.Compilation;

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