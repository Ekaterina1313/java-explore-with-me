package ru.practicum.mainService.controller.Private;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.dto.ParticipationRequestDto;
import ru.practicum.mainService.service.Private.PrivateRequestService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {
    private final PrivateRequestService requestService;

    public PrivateRequestController(PrivateRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto created(@PathVariable Integer userId, @RequestParam Integer eventId) {
        log.info("PRIVATE-controller: Поступил запрос на добавление request от пользователя с id = {}", userId);
        return requestService.create(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAll(@PathVariable Integer userId) {
        log.info("PRIVATE-controller: Поступил запрос на просмотр request от пользователя с id = {}", userId);
        return requestService.getAll(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable Integer userId, @PathVariable Integer requestId) {
        log.info("PRIVATE-controller: Поступил запрос на удаление запроса на участие в событии от пользователя " +
                "с id = {}", userId);
        return requestService.cancelRequest(userId, requestId);
    }
}