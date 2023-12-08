package ru.practicum.ewm.service;

import org.springframework.stereotype.Service;
import ru.practicum.common.dto.CompilationDto;
import ru.practicum.common.dto.EventShortDto;
import ru.practicum.common.dto.NewCompilationDto;
import ru.practicum.common.mapper.CompilationMapper;
import ru.practicum.common.mapper.EventMapper;
import ru.practicum.common.model.Compilation;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.EventCompilation;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventCompilationRepository;
import ru.practicum.ewm.repository.EventRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventCompilationRepository eventCompilationRepository;
    private final EventRepository eventRepository;

    public CompilationService(CompilationRepository compilationRepository,
                              EventCompilationRepository eventCompilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventCompilationRepository = eventCompilationRepository;
        this.eventRepository = eventRepository;
    }

    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation createdCompilation = compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto));
        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
        List<EventShortDto> eventShortDtos = events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        List<EventCompilation> eventCompilations = new ArrayList<>();
        for (Event element : events) {
            eventCompilations.add(new EventCompilation(null, element, createdCompilation));
        }
        eventCompilationRepository.saveAll(eventCompilations);
        return CompilationMapper.compilationDto(createdCompilation, eventShortDtos);
    }

    public void deleteById(Integer compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation with id=" + compId + " was not found"));
        eventCompilationRepository.deleteByCompilationId(compId);
        compilationRepository.deleteById(compId);
    }

    public CompilationDto update(Integer compId, NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation with id=" + compId + " was not found"));
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());
        compilationRepository.save(compilation);

        List<EventCompilation> eventCompilations = eventCompilationRepository.findAllById(List.of(compId));
        List<Integer> newIds = newCompilationDto.getEvents();
        List<Integer> toSave = new ArrayList<>();

        List<Integer> eventIds = new ArrayList<>();
        for (EventCompilation element : eventCompilations) {
            eventIds.add(element.getEvent().getId());
        }

        List<Integer> toRemove = new ArrayList<>();
        for (EventCompilation elem : eventCompilations) {
            if (!newIds.contains(elem.getEvent().getId())) {
                toRemove.add(elem.getEvent().getId());
            }
        }
        eventCompilationRepository.deleteByEventIds(toRemove);

        for (Integer element : newIds) {
            if (!eventIds.contains(element)) {
                toSave.add(element);
            }
        }

        List<Event> newEvents = new ArrayList<>();
        if (!toSave.isEmpty()) {
            newEvents = eventRepository.findAllById(toSave);
            List<EventCompilation> newEventCompilations = new ArrayList<>();
            for (Event element : newEvents) {
                newEventCompilations.add(new EventCompilation(null, element, compilation));
            }
            eventCompilationRepository.saveAll(newEventCompilations);
        }

        List<Event> eventsFromDb = eventCompilations.stream()
                .map(EventCompilation::getEvent)
                .collect(Collectors.toList());
        for (Event event : eventsFromDb) {
            if (newIds.contains(event.getId())) {
                newEvents.add(event);
            }
        }
        return CompilationMapper.compilationDto(compilation,
                newEvents.stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList()));
    }
}