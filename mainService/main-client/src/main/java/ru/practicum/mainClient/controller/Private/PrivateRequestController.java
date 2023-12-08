package ru.practicum.mainClient.controller.Private;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.ParticipationRequestDto;
import ru.practicum.mainClient.client.Private.PrivateRequestClient;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {
    private final PrivateRequestClient client;

    public PrivateRequestController(PrivateRequestClient client) {
        this.client = client;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> created(@PathVariable Integer userId, @RequestBody ParticipationRequestDto dto) {
        log.info("PRIVATE-controller: Поступил запрос на добавление request от пользователя с id = " + userId);
        try {
            return client.create(userId, dto);
        } catch (DataIntegrityViolationException ex) {
            log.error("PRIVATE-controller: Error creating participationRequest", ex);
            throw ex;
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAll(@PathVariable Integer userId) {
        log.info("PRIVATE-controller: Поступил запрос на просмотр request от пользователя с id = " + userId);
        try {
            return client.getAll(userId);
        } catch (DataIntegrityViolationException ex) {
            log.error("PRIVATE-controller: Error creating participationRequests", ex);
            throw ex;
        }
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> cancelRequest(@PathVariable Integer userId, @PathVariable Integer requestId) {
        log.info("PRIVATE-controller: Поступил запрос на удаление запроса на участие в событии от пользователя с id = " + userId);
        try {
            return client.cancelRequest(userId, requestId);
        } catch (DataIntegrityViolationException ex) {
            log.error("PRIVATE-controller: Error creating participationRequests", ex);
            throw ex;
        }
    }
}