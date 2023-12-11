package ru.practicum.mainService.service.Admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.mainService.dto.EventFullDto;
import ru.practicum.mainService.dto.UpdatedEventDto;
import ru.practicum.mainService.error.IncorrectParamException;
import ru.practicum.mainService.error.InvalidRequestException;
import ru.practicum.mainService.mapper.EventMapper;
import ru.practicum.mainService.model.Event;
import ru.practicum.mainService.model.StateAction;
import ru.practicum.mainService.model.States;
import ru.practicum.mainService.repository.CategoryRepository;
import ru.practicum.mainService.repository.EventRepository;
import ru.practicum.mainService.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminEventService {
    private final EventRepository eventRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AdminEventService(EventRepository eventRepository, UserRepository userRepository,
                             CategoryRepository categoryRepository, RestTemplate restTemplate) {
        this.eventRepository = eventRepository;
    }

    public List<EventFullDto> getEvents(List<Integer> users, List<String> states, List<Integer> categories,
                                        String rangeStart, String rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<Event> events;
        if (users.get(0) == 0 && categories.get(0) == 0) {
            events = eventRepository.getFilteredEventsWithoutUsersAndCategories(getStates(states),
                    LocalDateTime.parse(rangeStart, formatter),
                    LocalDateTime.parse(rangeEnd, formatter), pageable);
        } else if (categories.get(0) == 0) {
            events = eventRepository.getFilteredEventsWithoutCategories(users, getStates(states),
                    LocalDateTime.parse(rangeStart, formatter),
                    LocalDateTime.parse(rangeEnd, formatter), pageable);
        } else if (users.get(0) == 0) {
            events = eventRepository.getFilteredEventsWithoutUsers(getStates(states), categories,
                    LocalDateTime.parse(rangeStart, formatter),
                    LocalDateTime.parse(rangeEnd, formatter), pageable);
        } else {
            events = eventRepository.getFilteredEvents(users, getStates(states), categories,
                    LocalDateTime.parse(rangeStart, formatter),
                    LocalDateTime.parse(rangeEnd, formatter), pageable);
        }
        return events.getContent()
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    public EventFullDto update(Integer eventId, UpdatedEventDto updatedEvent) {
        LocalDateTime now = LocalDateTime.now();
        Event eventById = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" + eventId + " was not found"));
        validEventDate(eventById, now);
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

    private List<States> getStates(List<String> stringStates) {
        List<States> states = new ArrayList<>();
        for (String element : stringStates) {
            if (element.equalsIgnoreCase(States.PENDING.name())) {
                states.add(States.PENDING);
            } else if (element.equalsIgnoreCase(States.PUBLISHED.name())) {
                states.add(States.PUBLISHED);
            } else if (element.equalsIgnoreCase(States.CANCELED.name())) {
                states.add(States.CANCELED);
            } else {
                throw new InvalidRequestException("Неверно указано поле 'state'.");
            }
        }
        return states;
    }

    private void validEventDate(Event event, LocalDateTime time) {
        if (time.plusHours(1).isAfter(event.getEventDate())) {
            throw new IncorrectParamException("Field: eventDate. Error: должно содержать дату," +
                    " которая еще не наступила. Value: " + event.getEventDate());
        }
    }
}