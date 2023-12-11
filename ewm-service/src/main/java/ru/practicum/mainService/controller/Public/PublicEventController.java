package ru.practicum.mainService.controller.Public;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.dto.EventFullDto;
import ru.practicum.mainService.service.Public.PublicEventService;

import javax.servlet.http.HttpServletRequest;
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
            @RequestParam(name = "text") String text,
            @RequestParam(name = "categories") List<Integer> categories,
            @RequestParam(value = "paid") Boolean paid,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(name = "sort", defaultValue = "VIEWS") String sort,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size,
            HttpServletRequest request) {
        log.info("ADMIN-controller: Поступил запрос на просмотр событий в соответствии с параметрами сортировки");
        String clientIp = request.getRemoteAddr();
        log.info("client ip: {}", clientIp);
        String endpointPath = request.getRequestURI();
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
    public EventFullDto getById(@PathVariable Integer id, HttpServletRequest request) {
        log.info("PUBLIC-controller: Поступил запрос на просмотр события с id = ." + id);
        String clientIp = request.getRemoteAddr();
        log.info("client ip: {}", clientIp);
        String endpointPath = request.getRequestURI();
        log.info("endpoint path: {}", endpointPath);
        try {
            return eventService.getById(id, clientIp, endpointPath);
        } catch (DataIntegrityViolationException ex) {
            log.error("PUBLIC-controller: Error getting event", ex);
            throw ex;
        }
    }
}