package ru.practicum.mainService.service.Private;

import org.springframework.stereotype.Service;
import ru.practicum.mainService.dto.ParticipationRequestDto;
import ru.practicum.mainService.error.IncorrectParamException;
import ru.practicum.mainService.error.InvalidRequestException;
import ru.practicum.mainService.mapper.ParticipationRequestMapper;
import ru.practicum.mainService.model.*;
import ru.practicum.mainService.repository.EventRepository;
import ru.practicum.mainService.repository.RequestRepository;
import ru.practicum.mainService.service.ValidationById;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PrivateRequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final ValidationById validationById;

    public PrivateRequestService(RequestRepository requestRepository, EventRepository eventRepository,
                                 ValidationById validationById) {
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
        this.validationById = validationById;
    }

    public ParticipationRequestDto create(Integer userId, Integer eventId) {
        User userById = validationById.getUserById(userId);
        Event eventById = validationById.getEventById(eventId);
        if (Objects.equals(eventById.getInitiator().getId(), userId)) {
            throw new IncorrectParamException("Нельзя подать заявку на участие в собственном событии.");
        }
        if (!eventById.getState().equals(States.PUBLISHED)) {
            throw new IncorrectParamException("Нельзя участвовать в неопубликованном событии.");
        }
        if (eventById.getParticipantLimit() != 0 && Objects.equals(eventById.getParticipantLimit(),
                eventById.getConfirmedRequests())) {
            throw new IncorrectParamException("В выбранном мероприятии не осталось свободных мест.");
        }
        RequestStatus status = RequestStatus.PENDING;
        if (!eventById.getRequestModeration()) {
            status = RequestStatus.CONFIRMED;
            Integer newConfirmedRequests = eventById.getConfirmedRequests() + 1;
            eventById.setConfirmedRequests(newConfirmedRequests);
            eventRepository.save(eventById);
        }
        if (eventById.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }
        ParticipationRequest participationRequest = ParticipationRequestMapper.toParticipationRequest(null,
                LocalDateTime.now(), eventById, userById, status);
        return ParticipationRequestMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }

    public List<ParticipationRequestDto> getAll(Integer userId) {
        validationById.getUserById(userId);
        List<ParticipationRequest> userRequests = requestRepository.findAllByRequesterId(userId);
        return userRequests
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto cancelRequest(Integer userId, Integer requestId) {
        validationById.getUserById(userId);
        ParticipationRequest request = validationById.getParticipationRequestById(requestId);
        Event event = request.getEvent();
        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new InvalidRequestException("Нельзя отменить чужую заявку на участие в мероприятии.");
        }
        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            Integer confirmedRequest = request.getEvent().getConfirmedRequests() - 1;
            event.setConfirmedRequests(confirmedRequest);
            eventRepository.save(event);
        }
        request.setStatus(RequestStatus.CANCELED);
        return ParticipationRequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}