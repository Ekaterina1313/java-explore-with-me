package ru.practicum.mainService.service.Admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainService.GetFormatter;
import ru.practicum.mainService.dto.CommentShortDto;
import ru.practicum.mainService.dto.EventFullDto;
import ru.practicum.mainService.dto.UpdatedEventDto;
import ru.practicum.mainService.error.IncorrectParamException;
import ru.practicum.mainService.error.InvalidRequestException;
import ru.practicum.mainService.mapper.CommentMapper;
import ru.practicum.mainService.mapper.EventMapper;
import ru.practicum.mainService.model.Comment;
import ru.practicum.mainService.model.Event;
import ru.practicum.mainService.model.StateAction;
import ru.practicum.mainService.model.States;
import ru.practicum.mainService.repository.CommentRepository;
import ru.practicum.mainService.repository.EventRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminEventService {
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    public AdminEventService(EventRepository eventRepository, CommentRepository commentRepository) {
        this.eventRepository = eventRepository;
        this.commentRepository = commentRepository;
    }

    public List<EventFullDto> getEvents(List<Integer> users, List<String> states, List<Integer> categories,
                                        String rangeStart, String rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<Event> events;
        if ((users == null || users.get(0) == 0) && (categories == null || categories.get(0) == 0)) {
            events = eventRepository.getFilteredEventsWithoutUsersAndCategories(getStates(states),
                    LocalDateTime.parse(rangeStart, GetFormatter.getFormatter()),
                    LocalDateTime.parse(rangeEnd, GetFormatter.getFormatter()), pageable);
            System.out.println("параметр users = " + users);
            System.out.println("параметр states = " + states);
            System.out.println("параметр categories = " + categories);
            System.out.println("параметр rangeStart = " + rangeStart);
            System.out.println("параметр rangeEnd = " + rangeEnd);
        } else if (categories == null || categories.get(0) == 0) {
            events = eventRepository.getFilteredEventsWithoutCategories(users, getStates(states),
                    LocalDateTime.parse(rangeStart, GetFormatter.getFormatter()),
                    LocalDateTime.parse(rangeEnd, GetFormatter.getFormatter()), pageable);
        } else if (users == null || users.get(0) == 0) {
            events = eventRepository.getFilteredEventsWithoutUsers(getStates(states), categories,
                    LocalDateTime.parse(rangeStart, GetFormatter.getFormatter()),
                    LocalDateTime.parse(rangeEnd, GetFormatter.getFormatter()), pageable);
        } else {
            events = eventRepository.getFilteredEvents(users, getStates(states), categories,
                    LocalDateTime.parse(rangeStart, GetFormatter.getFormatter()),
                    LocalDateTime.parse(rangeEnd, GetFormatter.getFormatter()), pageable);
        }

        List<Event> eventsList = events.getContent();
        List<Comment> comments = commentRepository.findAllByEventIds(eventsList
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList()));
        return EventMapper.createEventFullDtoList(eventsList, comments);
    }

    public EventFullDto update(Integer eventId, UpdatedEventDto updatedEvent) {
        LocalDateTime now = LocalDateTime.now();
        Event eventById = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" + eventId + " was not found"));
        List<CommentShortDto> comments = commentRepository.findAllByEventIds(List.of(eventId))
                .stream()
                .map(CommentMapper::toCommentShortDto)
                .collect(Collectors.toList());

        if (updatedEvent.getAnnotation() != null) {
            validAnnotation(updatedEvent.getAnnotation());
            eventById.setAnnotation(updatedEvent.getAnnotation());
        }
        if (updatedEvent.getTitle() != null) {
            validTitle(updatedEvent.getTitle());
            eventById.setTitle(updatedEvent.getTitle());
        }
        if (updatedEvent.getDescription() != null) {
            validDescription(updatedEvent.getDescription());
            eventById.setDescription(updatedEvent.getDescription());
        }
        if (updatedEvent.getEventDate() != null) {
            validEventDate(updatedEvent.getEventDate(), now);
            eventById.setEventDate(LocalDateTime.parse(updatedEvent.getEventDate(), GetFormatter.getFormatter()));
        }
        if (updatedEvent.getPaid() != null) {
            eventById.setPaid(updatedEvent.getPaid());
        }
        if (updatedEvent.getParticipantLimit() != null) {
            eventById.setParticipantLimit(updatedEvent.getParticipantLimit());
        }
        if (updatedEvent.getStateAction() != null) {
            if (updatedEvent.getStateAction().equals(StateAction.PUBLISH_EVENT.name().toUpperCase())) {
                validPublishEventDate(eventById, now);
                if (eventById.getState().equals(States.PENDING)) {
                    eventById.setState(States.PUBLISHED);
                    eventById.setPublishedOn(LocalDateTime.now());
                    Event updateEvent = eventRepository.save(eventById);
                    return EventMapper.toEventFullDto(updateEvent, comments);
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
                    return EventMapper.toEventFullDto(updateEvent, comments);
                }
            } else {
                throw new InvalidRequestException("Неверно указано поле 'stateAction'.");
            }
        } else {
            return EventMapper.toEventFullDto(eventById, comments);
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

    private void validPublishEventDate(Event event, LocalDateTime time) {
        if (time.plusHours(1).minusSeconds(5).isAfter(event.getEventDate())) {
            throw new IncorrectParamException("Field: eventDate. Error: должно содержать дату," +
                    " которая еще не наступила. Value: " + event.getEventDate());
        }
    }

    private void validEventDate(String stringEventDate, LocalDateTime time) {
        LocalDateTime eventDate = LocalDateTime.parse(stringEventDate,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (time.plusHours(2).minusSeconds(5).isAfter(eventDate)) {
            throw new InvalidRequestException("Field: eventDate. Error: должно содержать дату," +
                    " которая еще не наступила. Value: " + stringEventDate);
        }
    }

    private void validDescription(String text) {
        if (text == null || text.isBlank() || text.length() < 20 || text.length() > 7000) {
            throw new InvalidRequestException("Field: description. Error: must not be null or blank, " +
                    "or less than 20 or more than 7000 chars." + text);
        }
    }

    private void validAnnotation(String text) {
        if (text == null || text.isBlank() || text.length() < 20 || text.length() > 2000) {
            throw new InvalidRequestException("Field: annotation. Error: must not be null or blank, or less than 20 " +
                    "char."
                    + text);
        }
    }

    private void validTitle(String title) {
        if (title == null || title.isBlank() || title.length() < 3 || title.length() > 120) {
            throw new InvalidRequestException("Field: title. Error: must not be null or blank, " +
                    "or less than 3 or more than 120 chars." + title);
        }
    }
}