package ru.practicum.mainService.service.Private;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainService.GetFormatter;
import ru.practicum.mainService.dto.*;
import ru.practicum.mainService.error.IncorrectParamException;
import ru.practicum.mainService.error.InvalidRequestException;
import ru.practicum.mainService.mapper.CommentMapper;
import ru.practicum.mainService.mapper.EventMapper;
import ru.practicum.mainService.mapper.ParticipationRequestMapper;
import ru.practicum.mainService.model.*;
import ru.practicum.mainService.repository.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PrivateEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;

    public PrivateEventService(EventRepository eventRepository, UserRepository userRepository,
                               CategoryRepository categoryRepository, RequestRepository requestRepository, CommentRepository commentRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
        this.commentRepository = commentRepository;
    }

    public EventFullDto create(EventDto eventDto, Integer userId) {
        User userById = getUserById(userId);
        Category categoryById = getCategoryById(eventDto.getCategory());
        Event createdEvent = eventRepository.save(EventMapper.fromEventDto(eventDto, categoryById,
                userById, 0, LocalDateTime.now(), 0));
        return EventMapper.toEventFullDto(createdEvent, new ArrayList<>());
    }

    public List<EventFullDto> getEvents(Integer userId, int from, int size) {
        getUserById(userId);
        Pageable pageable = PageRequest.of(from, size);
        Page<Event> eventsByUser = eventRepository.findEventsByInitiatorId(userId, pageable);
        List<Event> eventsList = eventsByUser.getContent();
        List<Comment> comments = commentRepository.findAllByEventIds(eventsList
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList()));
        return EventMapper.createEventFullDtoList(eventsList, comments);
    }

    public EventFullDto getById(Integer userId, Integer eventId) {
        getUserById(userId);
        Event eventById = getEventById(eventId);
        List<CommentShortDto> comments = commentRepository.findAllByEventIds(List.of(eventId))
                .stream()
                .map(CommentMapper::toCommentShortDto)
                .collect(Collectors.toList());
        if (!Objects.equals(eventById.getInitiator().getId(), userId)) {
            throw new InvalidRequestException("Пользователь не является организатором мероприятия.");
        }
        return EventMapper.toEventFullDto(eventById, comments);
    }

    public EventFullDto update(Integer userId, Integer eventId, UpdatedEventDto updatedEvent) {
        getUserById(userId);
        Event event = getEventById(eventId);

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new InvalidRequestException("Пользователь не является организатором мероприятия.");
        }
        if (event.getState().equals(States.PUBLISHED)) {
            throw new IncorrectParamException("Only pending or canceled event must changed");
        }
        if (updatedEvent.getAnnotation() != null && !updatedEvent.getAnnotation().isBlank()) {
            event.setAnnotation(updatedEvent.getAnnotation());
        }
        if (updatedEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updatedEvent.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException("Category with id=" + updatedEvent.getCategory() +
                            " was not found"));
            event.setCategory(category);
        }
        if (updatedEvent.getDescription() != null && !updatedEvent.getDescription().isBlank()) {
            event.setDescription(updatedEvent.getDescription());
        }
        if (updatedEvent.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updatedEvent.getEventDate(), GetFormatter.getFormatter()));
        }
        if (updatedEvent.getLocation() != null) {
            event.setLocationLat(updatedEvent.getLocation().getLat());
            event.setLocationLon(updatedEvent.getLocation().getLon());
        }
        if (updatedEvent.getPaid() != null) {
            event.setPaid(updatedEvent.getPaid());
        }
        if (updatedEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updatedEvent.getParticipantLimit());
        }
        if (updatedEvent.getRequestModeration() != null) {
            event.setRequestModeration(updatedEvent.getRequestModeration());
        }
        if (updatedEvent.getStateAction() != null) {
            if (updatedEvent.getStateAction().equalsIgnoreCase(StateAction.SEND_TO_REVIEW.name())) {
                event.setState(States.PENDING);
            } else if (updatedEvent.getStateAction().equalsIgnoreCase(StateAction.CANCEL_REVIEW.name().toUpperCase())) {
                event.setState(States.CANCELED);
            } else {
                throw new InvalidRequestException("Неверно указано поле 'stateAction'.");
            }
        }
        if (updatedEvent.getTitle() != null && !updatedEvent.getTitle().isBlank()) {
            event.setTitle(updatedEvent.getTitle());
        }

        List<CommentShortDto> comments = commentRepository.findAllByEventIds(List.of(eventId))
                .stream()
                .map(CommentMapper::toCommentShortDto)
                .collect(Collectors.toList());
        return EventMapper.toEventFullDto(eventRepository.save(event), comments);
    }

    public List<ParticipationRequestDto> getRequests(Integer userId, Integer eventId) {
        getUserById(userId);
        Event event = getEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new InvalidRequestException("Пользователь не является организатором события");
        }
        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult updateRequests(Integer userId, Integer eventId,
                                                         EventRequestStatusUpdateRequest request) {
        getUserById(userId);
        Event event = getEventById(eventId);
        if (event.getParticipantLimit() != 0 && Objects.equals(event.getParticipantLimit(),
                event.getConfirmedRequests())) {
            throw new IncorrectParamException("Достигнут лимит свободных мест.");
        }
        if (!event.getInitiator().getId().equals(userId)) {
            throw new InvalidRequestException("Пользователь не является организатором события");
        }
        List<ParticipationRequest> requestsByEvent = requestRepository.findAllById(request.getRequestIds());
        for (ParticipationRequest element : requestsByEvent) {
            if (!element.getEvent().getId().equals(eventId)) {
                throw new InvalidRequestException("Запрос с id = " + element.getId());
            }
            if (!element.getStatus().equals(RequestStatus.PENDING)) {
                throw new InvalidRequestException("Request must have status PENDING");
            }
        }
        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();
        if (request.getStatus().equalsIgnoreCase("CONFIRMED")) {
            for (ParticipationRequest element : requestsByEvent) {
                if (Objects.equals(event.getParticipantLimit(), event.getConfirmedRequests())) {
                    element.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(element);
                } else {
                    element.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(element);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                }
            }
        } else if (request.getStatus().equalsIgnoreCase("REJECTED")) {
            for (ParticipationRequest element : requestsByEvent) {
                element.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(element);
            }
        } else {
            throw new InvalidRequestException("Поле status может принимать значение CONFIRMED или REJECTED");
        }
        if (!confirmedRequests.isEmpty()) {
            eventRepository.save(event);
            requestRepository.saveAll(confirmedRequests);
        }
        if (!rejectedRequests.isEmpty()) {
            requestRepository.saveAll(rejectedRequests);
        }
        return new EventRequestStatusUpdateResult(
                confirmedRequests
                        .stream()
                        .map(ParticipationRequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList()),
                rejectedRequests
                        .stream()
                        .map(ParticipationRequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList()));
    }

    private User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " was not found"));
    }

    private Category getCategoryById(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id=" + categoryId + " was not found"));
    }

    private Event getEventById(Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" + eventId + " was not found"));
    }
}