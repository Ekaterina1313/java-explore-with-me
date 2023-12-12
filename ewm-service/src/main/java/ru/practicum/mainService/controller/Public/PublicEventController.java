package ru.practicum.mainService.controller.Public;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.dto.EventFullDto;
import ru.practicum.mainService.error.IncorrectParamException;
import ru.practicum.mainService.error.InvalidRequestException;
import ru.practicum.mainService.service.Public.PublicEventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/events")
public class PublicEventController {
    private final PublicEventService eventService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public PublicEventController(PublicEventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "categories", required = false) List<Integer> categories,
            @RequestParam(name = "paid", defaultValue = "true") Boolean paid,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "onlyAvailable", defaultValue = "true") Boolean onlyAvailable,
            @RequestParam(name = "sort", defaultValue = "VIEWS") String sort,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size,
            HttpServletRequest request) {
        log.info("ADMIN-controller: Поступил запрос на просмотр событий в соответствии с параметрами сортировки");
        String clientIp = request.getRemoteAddr();
        log.info("client ip: {}", clientIp);
        String endpointPath = request.getRequestURI();
        log.info("endpoint path: {}", endpointPath);
        log.info("Параметр size = " + size);
        if (rangeStart != null && rangeEnd != null) {
            if (LocalDateTime.parse(rangeStart, formatter).isAfter(LocalDateTime.parse(rangeEnd, formatter))) {
                throw new InvalidRequestException("Время начала события не должно позже времени его окончания.");
            }
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().format(formatter);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.of(9999, 1, 1, 0, 0, 0)
                    .format(formatter);
        }
        sort = sort.toLowerCase();
        if (sort.equals("views")) {
            sort = "views";
        } else if (sort.equals("event_date")) {
            sort = "eventDate";
        } else {
            throw new IncorrectParamException("Поле sort должно принимать значение EVENT_DATE или VIEWS," +
                    " текущее значение sort = " + sort);
        }
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