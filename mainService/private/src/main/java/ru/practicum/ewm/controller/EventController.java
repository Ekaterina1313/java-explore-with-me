package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.EventDto;
import ru.practicum.common.dto.EventFullDto;
import ru.practicum.common.dto.ParticipationRequestDto;
import ru.practicum.common.dto.UpdatedEventDto;
import ru.practicum.common.model.EventRequestStatusUpdateRequest;
import ru.practicum.common.model.EventRequestStatusUpdateResult;
import ru.practicum.ewm.service.EventService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@RequestBody EventDto eventDto, @PathVariable Integer userId) {
        log.info("PRIVATE-controller: Поступил запрос на добавление нового события = " + eventDto.getId());
        return eventService.create(eventDto, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(@PathVariable Integer userId,
                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("PRIVATE-controller: Поступил запрос на просмотр событий от пользователя с id = " + userId);
        try {
            return eventService.getEvents(userId, from, size);
        } catch (DataIntegrityViolationException ex) {
            log.error("PUBLIC-controller: Error getting event", ex);
            throw ex;
        }
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getById(@PathVariable Integer userId, @PathVariable Integer eventId) {
        log.info("PRIVATE-controller: Поступил запрос на просмотр события с id = " + eventId
                + " пользователем  с id = " + userId);
        try {
            return eventService.getById(userId, eventId);
        } catch (DataIntegrityViolationException ex) {
            log.error("PUBLIC-controller: Error getting event", ex);
            throw ex;
        }
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@PathVariable Integer userId, @PathVariable Integer eventId,
                               @RequestBody UpdatedEventDto updatedEvent) {
        log.info("PRIVATE-controller: Поступил запрос на обновление информации о событии с id = " + userId +
                " пользователем с id = " + updatedEvent.getId());
        return eventService.update(userId, eventId, updatedEvent);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(@PathVariable Integer userId, @PathVariable Integer eventId) {
        log.info("PRIVATE-controller: Поступил запрос на просмотр события с id = " + eventId
                + " пользователем  с id = " + userId);
        try {
            return eventService.getRequests(userId, eventId);
        } catch (DataIntegrityViolationException ex) {
            log.error("PRIVATE-controller: Error getting request", ex);
            throw ex;
        }
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequests(@PathVariable Integer userId, @PathVariable Integer eventId,
                                                         @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("PRIVATE-controller: Поступил запрос на обновление статуса заявок.");
        return eventService.updateRequests(userId, eventId, request);
    }
}