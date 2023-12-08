package ru.practicum.mainClient.controller.Public;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainClient.client.Public.PublicEventClient;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/events")
public class PublicEventController {
    private final PublicEventClient client;

    public PublicEventController(PublicEventClient client) {
        this.client = client;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getEvents(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "categories", required = false) List<Integer> categories,
            @RequestParam(value = "paid", defaultValue = "false") Boolean paid,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(value = "sort", defaultValue = "VIEWS") String sort,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size,
            HttpServletRequest request) {
        log.info("ADMIN-controller: Поступил запрос на просмотр событий в соответствии с параметрами сортировки");
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Client-Ip", request.getRemoteAddr());
        httpHeaders.add("X-Endpoint-Path", request.getRequestURI());
        try {
            return client.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                    httpHeaders);
        } catch (DataIntegrityViolationException ex) {
            log.error("PUBLIC-controller: Error getting events", ex);
            throw ex;
        }

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getById(@PathVariable Integer id, HttpServletRequest request) {
        log.info("PUBLIC-controller: Поступил запрос на просмотр события с id = ." + id);
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Client-Ip", request.getRemoteAddr());
        httpHeaders.add("X-Endpoint-Path", request.getRequestURI());
        try {
            return client.getById(id, httpHeaders);
        } catch (DataIntegrityViolationException ex) {
            log.error("PUBLIC-controller: Error getting event", ex);
            throw ex;
        }
    }
}