package ru.practicum.mainService.controller.Private;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.dto.CommentDto;
import ru.practicum.mainService.error.InvalidRequestException;
import ru.practicum.mainService.service.Private.PrivateCommentService;

import java.time.LocalDateTime;
import java.util.List;

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
        validText(commentDto.getText());
        return commentService.create(commentDto, userId, eventId, now);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto update(@RequestBody CommentDto updatedCommentDto, @PathVariable Integer userId,
                             @PathVariable Integer commentId) {
        log.info("PRIVATE-controller: Поступил запрос на обновление комментария с id = {}", commentId);
        if (updatedCommentDto.getText() != null) {
            validText(updatedCommentDto.getText());
        }
        return commentService.update(updatedCommentDto, userId, commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAll(@PathVariable Integer userId) {
        log.info("PRIVATE-controller: Поступил запрос на просмотр всех комментариев пользователя с id = {}", userId);
        return commentService.getAll(userId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer userId, @PathVariable Integer commentId) {
        log.info("PRIVATE-controller: Поступил запрос на удаление комментария с id = {} пользователем с id= {}",
                commentId, userId);
        commentService.delete(userId, commentId);
    }

    private void validText(String text) {
        if (text == null || text.isBlank() || text.length() < 10 || text.length() > 5000) {
            throw new InvalidRequestException("Field: text. Error: must not be null or blank, " +
                    "or less than 10 or more than 5000 chars. Value: " + text);
        }
    }
}