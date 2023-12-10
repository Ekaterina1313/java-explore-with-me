package ru.practicum.mainService.controller.Admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.dto.EventFullDto;
import ru.practicum.mainService.dto.UpdatedEventDto;
import ru.practicum.mainService.error.IncorrectParamException;
import ru.practicum.mainService.service.Admin.AdminventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/events")
public class AdminEventController {
    private final AdminventService eventService;

    public AdminEventController(AdminventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(
            @RequestParam(name = "users", required = false) List<Integer> users,
            @RequestParam(name = "states", required = false) List<String> states,
            @RequestParam(name = "categories", required = false) List<Integer> categories,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("ADMIN-controller: Поступил запрос на просмотр событий для юзеров с id = " + users);
        try {
            return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error creating event", ex);
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