package ru.practicum.ewm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.common.dto.EventFullDto;
import ru.practicum.common.dto.UpdatedEventDto;
import ru.practicum.common.error.IncorrectParamException;
import ru.practicum.common.error.InvalidRequestException;
import ru.practicum.common.mapper.EventMapper;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.StateAction;
import ru.practicum.common.model.States;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate;
    private static String app = "mainService/admin";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventService(EventRepository eventRepository, UserRepository userRepository,
                        CategoryRepository categoryRepository, RestTemplate restTemplate) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.restTemplate = restTemplate;
    }

    public List<EventFullDto> getEvents(List<Integer> users, List<String> states, List<Integer> categories,
                                        String rangeStart, String rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<Event> events = null;
        if (users == null && states == null && categories == null && rangeStart == null && rangeEnd == null) {
            events = eventRepository.findAll(pageable);
        }
        if (users != null) {
            events = eventRepository.findAllByInitiatorIdIn(users, pageable);
        }
        if (states != null) {
            List<States> enumStates = states.stream()
                    .map(States::valueOf)
                    .collect(Collectors.toList());
            events = eventRepository.findAllByStateIn(enumStates, pageable);
        }
        if (categories != null) {
            events = eventRepository.findAllByCategoryIdIn(categories, pageable);
        }
        if (rangeStart != null && rangeEnd != null) {
            events = eventRepository.findAllByCreatedOnBetween(
                    LocalDateTime.parse(rangeStart, formatter),
                    LocalDateTime.parse(rangeEnd, formatter),
                    pageable);
        }

        if (rangeStart != null && rangeEnd == null) {
            events = eventRepository.findAllByCreatedOnAfter(LocalDateTime.parse(rangeStart, formatter), pageable);
        }
        if (rangeEnd != null && rangeStart == null) {
            events = eventRepository.findAllByCreatedOnBefore(LocalDateTime.parse(rangeEnd, formatter), pageable);
        }

        if (users != null && states != null && categories != null && rangeStart != null && rangeEnd != null) {
            events = eventRepository.getFilteredEvents(users, states.stream().map(States::valueOf).collect(Collectors.toList()),
                    categories, LocalDateTime.parse(rangeStart, formatter),
                    LocalDateTime.parse(rangeEnd, formatter), pageable);
        }
        return events.getContent()
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    public EventFullDto update(Integer eventId, UpdatedEventDto updatedEvent) {
        Event eventById = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" + eventId + " was not found"));

        if (updatedEvent.getStateAction().equals(StateAction.PUBLISH_EVENT.name().toUpperCase())) {
            if (eventById.getState().equals(States.PENDING)) {
                eventById.setState(States.PUBLISHED);
                eventById.setPublishedOn(LocalDateTime.now());
                Event updateEvent = eventRepository.save(eventById);
                return EventMapper.toEventFullDto(updateEvent);
            } else {
                throw new IncorrectParamException("Cannot publish the event because it's not in the right state: " +
                        eventById.getState().name());
            }
        } else if (updatedEvent.getStateAction().equals(StateAction.REJECT_EVENT.name().toUpperCase())) {
            if (eventById.getState().equals(States.PUBLISHED)) {
                throw new IncorrectParamException("Cannot reject the event because it's not in the right state: " +
                        eventById.getState().name());
            } else {
                eventById.setState(States.CANCELED);
                Event updateEvent = eventRepository.save(eventById);
                return EventMapper.toEventFullDto(updateEvent);
            }
        } else {
            throw new InvalidRequestException("Неверно указано поле 'stateAction'.");
        }
    }
}