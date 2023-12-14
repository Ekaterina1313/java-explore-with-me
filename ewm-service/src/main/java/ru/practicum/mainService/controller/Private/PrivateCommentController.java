package ru.practicum.mainService.controller.Private;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.dto.CommentDto;
import ru.practicum.mainService.dto.EventDto;
import ru.practicum.mainService.dto.EventFullDto;
import ru.practicum.mainService.service.Private.PrivateCommentService;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {
    private final PrivateCommentService commentService;

    public PrivateCommentController(PrivateCommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@RequestBody CommentDto commentDto, @PathVariable Integer userId,
                             @PathVariable Integer eventId) {
        LocalDateTime now = LocalDateTime.now();
        log.info("PRIVATE-controller: Поступил запрос на добавление нового комментария к событию с id = {}", eventId);
        return commentService.create(commentDto, userId, eventId, now);
    }



}
