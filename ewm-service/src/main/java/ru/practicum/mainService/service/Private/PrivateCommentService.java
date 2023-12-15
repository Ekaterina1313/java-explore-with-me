package ru.practicum.mainService.service.Private;

import org.springframework.stereotype.Service;
import ru.practicum.mainService.dto.CommentDto;
import ru.practicum.mainService.error.InvalidRequestException;
import ru.practicum.mainService.mapper.CommentMapper;
import ru.practicum.mainService.model.Comment;
import ru.practicum.mainService.model.Event;
import ru.practicum.mainService.model.States;
import ru.practicum.mainService.model.User;
import ru.practicum.mainService.repository.CommentRepository;
import ru.practicum.mainService.service.ValidationById;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PrivateCommentService {
    private final CommentRepository commentRepository;
    private final ValidationById validationById;

    public PrivateCommentService(CommentRepository commentRepository, ValidationById validationById) {
        this.commentRepository = commentRepository;
        this.validationById = validationById;
    }

    public CommentDto create(CommentDto commentDto, Integer userId, Integer eventId, LocalDateTime time) {
        User author = validationById.getUserById(userId);
        Event event = validationById.getEventById(eventId);
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new InvalidRequestException("Нельзя оставлять комментарии к своим событиям.");
        }
        if (!Objects.equals(event.getState(), States.PUBLISHED)) {
            throw new InvalidRequestException("Нельзя оставлять комментарии к неопубликованным событиям.");
        }
        Comment createdComment = CommentMapper.fromCommentDto(commentDto, author, event, time);
        return CommentMapper.toCommentDto(commentRepository.save(createdComment));
    }

    public CommentDto update(CommentDto updatedCommentDto, Integer userId, Integer commentId) {
        validationById.getUserById(userId);
        Comment commentById = validationById.getCommentById(commentId);
        if (!Objects.equals(commentById.getAuthor().getId(), userId)) {
            throw new InvalidRequestException("Нельзя изменить чужой комментарий.");
        }
        if (updatedCommentDto.getText() != null) {
            commentById.setText(updatedCommentDto.getText());
        }
        return CommentMapper.toCommentDto(commentRepository.save(commentById));
    }

    public List<CommentDto> getAll(Integer userId) {
        validationById.getUserById(userId);
        return commentRepository.findAllByAuthorId(List.of(userId))
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public void delete(Integer userId, Integer commentId) {
        validationById.getUserById(userId);
        Comment comment = validationById.getCommentById(commentId);
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new InvalidRequestException("Нельзя удалить чужой комментарий.");
        }
        commentRepository.deleteById(commentId);
    }
}