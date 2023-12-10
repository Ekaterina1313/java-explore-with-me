package ru.practicum.mainService.controller.Admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.dto.EventFullDto;
import ru.practicum.mainService.dto.UpdatedEventDto;
import ru.practicum.mainService.error.IncorrectParamException;
import ru.practicum.mainService.service.Admin.AdminEventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/events")
public class AdminEventController {
    private final AdminEventService eventService;

    public AdminEventController(AdminEventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(
            @RequestParam(name = "users") List<Integer> users,
            @RequestParam(name = "states") List<String> states,
            @RequestParam(name = "categories") List<Integer> categories,
            @RequestParam(name = "rangeStart") String rangeStart,
            @RequestParam(value = "rangeEnd") String rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("ADMIN-controller: Поступил запрос на просмотр событий.");
        try {
            return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error getting event", ex);
            throw ex;
        }

    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@PathVariable Integer eventId, @RequestBody UpdatedEventDto updatedEvent) {
        log.info("ADMIN-controller: Поступил запрос на обновление события с id = " + eventId);
        validEventDate(updatedEvent);
        return eventService.update(eventId, updatedEvent);
    }

    private void validEventDate(UpdatedEventDto upEvent) {
        LocalDateTime eventDate = LocalDateTime.parse(upEvent.getEventDate(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (LocalDateTime.now().plusHours(1).isAfter(eventDate)) {
            throw new IncorrectParamException("Field: eventDate. Error: должно содержать дату," +
                    " которая еще не наступила. Value: " + upEvent.getEventDate());
        }
    }
}