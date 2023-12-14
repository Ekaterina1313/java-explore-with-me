package ru.practicum.mainService.mapper;

import ru.practicum.mainService.GetFormatter;
import ru.practicum.mainService.dto.CommentDto;
import ru.practicum.mainService.dto.CommentShortDto;
import ru.practicum.mainService.model.Comment;
import ru.practicum.mainService.model.Event;
import ru.practicum.mainService.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                UserMapper.toUserShortDto(comment.getAuthor()),
                comment.getEvent().getId(),
                comment.getCreated().format(GetFormatter.getFormatter())
        );
    }

    public static Comment fromCommentDto(CommentDto commentDto, User author, Event event, LocalDateTime time) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                author,
                event,
                time
        );
    }

    public static CommentShortDto toCommentShortDto(Comment comment) {
        return new CommentShortDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getId()
        );
    }
}
