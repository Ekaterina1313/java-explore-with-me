package ru.practicum.mainService.service.Admin;

import org.springframework.stereotype.Service;
import ru.practicum.mainService.dto.CompilationDto;
import ru.practicum.mainService.dto.EventShortDto;
import ru.practicum.mainService.dto.NewCompilationDto;
import ru.practicum.mainService.mapper.CompilationMapper;
import ru.practicum.mainService.mapper.EventMapper;
import ru.practicum.mainService.model.Compilation;
import ru.practicum.mainService.model.Event;
import ru.practicum.mainService.model.EventCompilation;
import ru.practicum.mainService.repository.CompilationRepository;
import ru.practicum.mainService.repository.EventCompilationRepository;
import ru.practicum.mainService.repository.EventRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final EventCompilationRepository eventCompilationRepository;
    private final EventRepository eventRepository;

    public AdminCompilationService(CompilationRepository compilationRepository,
                                   EventCompilationRepository eventCompilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventCompilationRepository = eventCompilationRepository;
        this.eventRepository = eventRepository;
    }

    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation createdCompilation = compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto));
        if (newCompilationDto.getEvents() == null || newCompilationDto.getEvents().isEmpty()) {
            return CompilationMapper.compilationDto(createdCompilation, new ArrayList<>());
        }
        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
        List<EventShortDto> eventShortDtos = events
                .stream()
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
        if (newCompilationDto.getPinned() != null) {
            compilation.setPinned(newCompilationDto.getPinned());
        }
        if (newCompilationDto.getTitle() != null) {
            compilation.setTitle(newCompilationDto.getTitle());
        }
        compilationRepository.save(compilation);
        List<EventCompilation> eventCompilations = eventCompilationRepository.findAllById(List.of(compId));
        if (newCompilationDto.getEvents() == null) {
            List<Event> eventsToReturn = eventCompilations
                    .stream()
                    .map(EventCompilation::getEvent)
                    .collect(Collectors.toList());
            return CompilationMapper.compilationDto(compilation, eventsToReturn
                    .stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList()));
        }
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
        if (!toRemove.isEmpty()) {
            eventCompilationRepository.deleteByEventIds(toRemove);
        }

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

        List<Event> eventsFromDb = eventCompilations
                .stream()
                .map(EventCompilation::getEvent)
                .collect(Collectors.toList());
        for (Event event : eventsFromDb) {
            if (newIds.contains(event.getId())) {
                newEvents.add(event);
            }
        }
        return CompilationMapper.compilationDto(compilation, newEvents
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList()));
    }
}