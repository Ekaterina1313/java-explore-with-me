package ru.practicum.mainService.controller.Admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.GetFormatter;
import ru.practicum.mainService.dto.EventFullDto;
import ru.practicum.mainService.dto.UpdatedEventDto;
import ru.practicum.mainService.service.Admin.AdminEventService;

import java.time.LocalDateTime;
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
            @RequestParam(name = "users", required = false) List<Integer> users,
            @RequestParam(name = "states", required = false) List<String> states,
            @RequestParam(name = "categories", required = false) List<Integer> categories,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("ADMIN-controller: Поступил запрос на просмотр событий.");
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().format(GetFormatter.getFormatter());
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.of(9999, 1, 1, 0, 0, 0)
                    .format(GetFormatter.getFormatter());
        }
        if (states == null) {
            states = List.of("PUBLISHED", "PENDING", "CANCELED");
        }
        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@PathVariable Integer eventId, @RequestBody UpdatedEventDto updatedEvent) {
        log.info("ADMIN-controller: Поступил запрос на обновление события с id = {}", eventId);
        return eventService.update(eventId, updatedEvent);
    }
}