package ru.practicum.mainService.controller.Admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.dto.EventFullDto;
import ru.practicum.mainService.dto.UpdatedEventDto;
import ru.practicum.mainService.service.Admin.AdminEventService;

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
        /*if (updatedEvent.getStateAction() == null) {
            throw new InvalidRequestException("Поле stateAction не должно быть = null");
        }*/
        return eventService.update(eventId, updatedEvent);
    }
}