package ru.practicum.mainService.service.Public;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainService.dto.CompilationDto;
import ru.practicum.mainService.dto.EventShortDto;
import ru.practicum.mainService.mapper.CompilationMapper;
import ru.practicum.mainService.mapper.EventMapper;
import ru.practicum.mainService.model.Compilation;
import ru.practicum.mainService.model.Event;
import ru.practicum.mainService.model.EventCompilation;
import ru.practicum.mainService.repository.CompilationRepository;
import ru.practicum.mainService.repository.EventCompilationRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PublicCompilationService {
    private final CompilationRepository compilationRepository;
    private final EventCompilationRepository eventCompilationRepository;

    public PublicCompilationService(CompilationRepository compilationRepository,
                                    EventCompilationRepository eventCompilationRepository) {
        this.compilationRepository = compilationRepository;
        this.eventCompilationRepository = eventCompilationRepository;
    }

    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable).getContent();
        List<Integer> compilationIds = compilations
                .stream()
                .map(Compilation::getId)
                .collect(Collectors.toList());
        List<EventCompilation> eventCompilations = eventCompilationRepository.findAllByCompilationIds(compilationIds);

        List<CompilationDto> compilationDtos = new ArrayList<>();
        for (Compilation element : compilations) {
            List<Event> events = new ArrayList<>();
            for (EventCompilation eventCompilation : eventCompilations) {
                if (Objects.equals(element.getId(), eventCompilation.getCompilation().getId())) {
                    events.add(eventCompilation.getEvent());
                }
            }
            List<EventShortDto> eventShortDtos = events
                    .stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());
            compilationDtos.add(CompilationMapper.compilationDto(element, eventShortDtos));
        }
        return compilationDtos;
    }

    public CompilationDto getById(Integer compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation with id=" + compId + " was not found"));
        List<EventCompilation> eventCompilations = eventCompilationRepository.findAllByCompilationIds(List.of(compId));
        List<Event> events = eventCompilations
                .stream()
                .map(EventCompilation::getEvent)
                .collect(Collectors.toList());
        List<EventShortDto> eventShortDtos = events
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        return CompilationMapper.compilationDto(compilation, eventShortDtos);
    }
}