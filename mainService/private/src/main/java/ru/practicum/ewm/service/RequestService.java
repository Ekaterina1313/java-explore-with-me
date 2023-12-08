package ru.practicum.ewm.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.common.dto.ParticipationRequestDto;
import ru.practicum.common.error.IncorrectParamException;
import ru.practicum.common.error.InvalidRequestException;
import ru.practicum.common.mapper.ParticipationRequestMapper;
import ru.practicum.common.model.*;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RequestService(RequestRepository requestRepository,
                          UserRepository userRepository, EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public ParticipationRequestDto create(Integer userId, ParticipationRequestDto dto) {
        User userById = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " was not found"));
        Event eventById = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" + dto.getEventId() + " was not found"));
        if (Objects.equals(eventById.getInitiator().getId(), userId)) {
            throw new IncorrectParamException("Нельзя подать заявку на участие в собственном событии.");
        }
        if (!eventById.getState().equals(States.PUBLISHED)) {
            throw new IncorrectParamException("Нельзя участвовать в неопубликованном событии.");
        }
        if (Objects.equals(eventById.getParticipantLimit(), eventById.getConfirmedRequests())) {
            throw new IncorrectParamException("В выбранном мероприятии не осталось свободных мест.");
        }
        Status status = Status.PENDING;
        if (!eventById.getRequestModeration()) {
            status = Status.CONFIRMED;
            Integer newConfirmedRequests = eventById.getConfirmedRequests() + 1;
            eventById.setConfirmedRequests(newConfirmedRequests);
            eventRepository.save(eventById);
        }
        if (eventById.getParticipantLimit() == 0) {
            status = Status.REJECTED;
        }
        ParticipationRequest participationRequest = ParticipationRequestMapper.toParticipationRequest(null,
                LocalDateTime.now(), eventById, userById, status);
        return ParticipationRequestMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }

    public List<ParticipationRequestDto> getAll(@PathVariable Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " was not found"));
        List<ParticipationRequest> userRequests = requestRepository.findAllByRequesterId(userId);

        return userRequests.stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto cancelRequest(Integer userId, Integer requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " was not found"));
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request with id=" + requestId + " was not found"));
        Event event = request.getEvent();
        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new InvalidRequestException("Нельзя отменить чужую заявку на участие в мероприятии.");
        }
        Integer confirmedRequest = request.getEvent().getConfirmedRequests() - 1;
        event.setConfirmedRequests(confirmedRequest);
        eventRepository.save(event);
        return ParticipationRequestMapper.toParticipationRequestDto(request);
    }
}