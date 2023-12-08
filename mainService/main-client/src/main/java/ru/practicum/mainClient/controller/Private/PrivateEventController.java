package ru.practicum.mainClient.controller.Private;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.EventDto;
import ru.practicum.common.dto.UpdatedEventDto;
import ru.practicum.common.error.IncorrectParamException;
import ru.practicum.common.error.InvalidRequestException;
import ru.practicum.common.model.EventRequestStatusUpdateRequest;
import ru.practicum.mainClient.client.Private.PrivateEventClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final PrivateEventClient client;

    public PrivateEventController(PrivateEventClient client) {
        this.client = client;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody EventDto eventDto, @PathVariable Integer userId) {
        log.info("CLIENT-PRIVATE-controller: Поступил запрос на добавление нового события = " + eventDto.getId());
        validCategory(eventDto);
        validEventDate(eventDto.getEventDate());
        validParticipantLimit(eventDto.getParticipantLimit());
        return client.create(eventDto, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getEvents(@PathVariable Integer userId,
                                            @RequestParam(name = "from", defaultValue = "0") int from,
                                            @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("CLIENT-PRIVATE-controller: Поступил запрос на просмотр событий от пользователя с id = " + userId);
        try {
            return client.getEvents(userId, from, size);
        } catch (DataIntegrityViolationException ex) {
            log.error("PUBLIC-controller: Error getting event", ex);
            throw ex;
        }
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getById(@PathVariable Integer userId, @PathVariable Integer eventId) {
        log.info("CLIENT-PRIVATE-controller: Поступил запрос на просмотр события с id = " + eventId
                + " пользователем  с id = " + userId);
        try {
            return client.getById(userId, eventId);
        } catch (DataIntegrityViolationException ex) {
            log.error("PUBLIC-controller: Error getting event", ex);
            throw ex;
        }
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@PathVariable Integer userId, @PathVariable Integer eventId,
                                         @RequestBody UpdatedEventDto updatedEvent) {
        log.info("PRIVATE-controller: Поступил запрос на обновление информации о событии с id = " + userId +
                " пользователем с id = " + updatedEvent.getId());
        if (updatedEvent.getEventDate() != null) {
            validEventDate(updatedEvent.getEventDate());
        }
        if (updatedEvent.getParticipantLimit() != null) {
            validParticipantLimit(updatedEvent.getParticipantLimit());
        }
        return client.update(userId, eventId, updatedEvent);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequests(@PathVariable Integer userId, @PathVariable Integer eventId) {
        log.info("PRIVATE-controller: Поступил запрос на просмотр события с id = " + eventId
                + " пользователем  с id = " + userId);
        try {
            return client.getRequests(userId, eventId);
        } catch (DataIntegrityViolationException ex) {
            log.error("PRIVATE-controller: Error getting request", ex);
            throw ex;
        }
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateRequests(@PathVariable Integer userId, @PathVariable Integer eventId,
                                                 @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("PRIVATE-controller: Поступил запрос на обновление статуса заявок.");
        return client.updateRequests(userId, eventId, request);
    }

    private void validParticipantLimit(Integer limit) {
        if (limit <= 0) {
            throw new InvalidRequestException("Field: ParticipantLimit. Error: must not be 0 or less. Value: " + limit);
        }
    }

    private void validCategory(EventDto eventDto) {
        if (eventDto.getCategory() == null) {
            throw new InvalidRequestException("Field: category. Error: must not be blank. Value: null");
        }
    }

    private void validEventDate(String stringEventDate) {
        LocalDateTime eventDate = LocalDateTime.parse(stringEventDate,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (LocalDateTime.now().plusHours(2).isAfter(eventDate)) {
            throw new IncorrectParamException("Field: eventDate. Error: должно содержать дату," +
                    " которая еще не наступила. Value: " + stringEventDate);
        }
    }
}