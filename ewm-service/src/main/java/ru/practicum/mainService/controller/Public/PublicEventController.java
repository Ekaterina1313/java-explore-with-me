package ru.practicum.mainService.controller.Public;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.dto.EventFullDto;
import ru.practicum.mainService.service.Public.PublicEventService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/events")
public class PublicEventController {
    private final PublicEventService eventService;

    public PublicEventController(PublicEventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "categories", required = false) List<Integer> categories,
            @RequestParam(value = "paid", defaultValue = "false") Boolean paid,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(value = "sort", defaultValue = "VIEWS") String sort,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestHeader(name = "X-Client-Ip", required = false) String clientIp,
            @RequestHeader(name = "X-Endpoint-Path", required = false) String endpointPath) {
        log.info("ADMIN-controller: Поступил запрос на просмотр событий в соответствии с параметрами сортировки");
        log.info("client ip: {}", clientIp);
        log.info("endpoint path: {}", endpointPath);
        try {
            return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                    clientIp, endpointPath);
        } catch (DataIntegrityViolationException ex) {
            log.error("PUBLIC-controller: Error getting events", ex);
            throw ex;
        }

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getById(@PathVariable Integer id,
                                @RequestHeader(name = "X-Client-Ip", required = false) String clientIp,
                                @RequestHeader(name = "X-Endpoint-Path", required = false) String endpointPath) {
        log.info("PUBLIC-controller: Поступил запрос на просмотр события с id = ." + id);
        log.info("client ip: {}", clientIp);
        log.info("endpoint path: {}", endpointPath);
        try {
            return eventService.getById(id, clientIp, endpointPath);
        } catch (DataIntegrityViolationException ex) {
            log.error("PUBLIC-controller: Error getting event", ex);
            throw ex;
        }
    }
}