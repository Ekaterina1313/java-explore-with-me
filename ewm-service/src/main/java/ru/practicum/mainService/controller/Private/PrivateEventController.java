package ru.practicum.mainService.controller.Private;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.dto.EventDto;
import ru.practicum.mainService.dto.EventFullDto;
import ru.practicum.mainService.dto.ParticipationRequestDto;
import ru.practicum.mainService.dto.UpdatedEventDto;
import ru.practicum.mainService.error.InvalidRequestException;
import ru.practicum.mainService.model.EventRequestStatusUpdateRequest;
import ru.practicum.mainService.model.EventRequestStatusUpdateResult;
import ru.practicum.mainService.service.Private.PrivateEventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final PrivateEventService eventService;

    public PrivateEventController(PrivateEventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@RequestBody EventDto eventDto, @PathVariable Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        log.info("PRIVATE-controller: Поступил запрос на добавление нового события = {}", eventDto.getId());
        if (eventDto.getPaid() == null) {
            eventDto.setPaid(false);
        }
        if (eventDto.getParticipantLimit() == null) {
            eventDto.setParticipantLimit(0);
        }
        if (eventDto.getRequestModeration() == null) {
            eventDto.setRequestModeration(true);
        }
        validDescription(eventDto.getDescription());
        validAnnotation(eventDto.getAnnotation());
        validTitle(eventDto.getTitle());
        validCategory(eventDto);
        validEventDate(eventDto.getEventDate(), now);
        validParticipantLimit(eventDto.getParticipantLimit());
        return eventService.create(eventDto, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(@PathVariable Integer userId,
                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("PRIVATE-controller: Поступил запрос на просмотр событий от пользователя с id = {}", userId);
        return eventService.getEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getById(@PathVariable Integer userId, @PathVariable Integer eventId) {
        log.info("PRIVATE-controller: Поступил запрос на просмотр события с id = {}", eventId
                + " пользователем  с id = " + userId);
        return eventService.getById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@PathVariable Integer userId, @PathVariable Integer eventId,
                               @RequestBody UpdatedEventDto updatedEvent) {
        LocalDateTime now = LocalDateTime.now();
        log.info("PRIVATE-controller: Поступил запрос на обновление информации о событии с id = {}", userId +
                " пользователем с id = " + updatedEvent.getId());
        if (updatedEvent.getDescription() != null) {
            validDescription(updatedEvent.getDescription());
        }
        if (updatedEvent.getAnnotation() != null) {
            validAnnotation(updatedEvent.getAnnotation());
        }
        if (updatedEvent.getTitle() != null) {
            validTitle(updatedEvent.getTitle());
        }
        if (updatedEvent.getEventDate() != null) {
            validEventDate(updatedEvent.getEventDate(), now);
        }
        if (updatedEvent.getParticipantLimit() != null) {
            validParticipantLimit(updatedEvent.getParticipantLimit());
        }
        return eventService.update(userId, eventId, updatedEvent);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(@PathVariable Integer userId, @PathVariable Integer eventId) {
        log.info("PRIVATE-controller: Поступил запрос на просмотр события с id = {} пользователем  с id = {}",
                eventId, userId);
        return eventService.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequests(@PathVariable Integer userId, @PathVariable Integer eventId,
                                                         @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("PRIVATE-controller: Поступил запрос на обновление статуса заявок.");
        return eventService.updateRequests(userId, eventId, request);
    }

    private void validParticipantLimit(Integer limit) {
        if (limit < 0) {
            throw new InvalidRequestException("Field: ParticipantLimit. Error: must not be less than 0. Value: " + limit);
        }
    }

    private void validCategory(EventDto eventDto) {
        if (eventDto.getCategory() == null) {
            throw new InvalidRequestException("Field: category. Error: must not be blank. Value: null");
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