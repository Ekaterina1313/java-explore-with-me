package ru.practicum.mainService.service.Private;

import org.springframework.stereotype.Service;
import ru.practicum.mainService.dto.CommentDto;
import ru.practicum.mainService.mapper.CommentMapper;
import ru.practicum.mainService.model.Comment;
import ru.practicum.mainService.model.Event;
import ru.practicum.mainService.model.User;
import ru.practicum.mainService.repository.CommentRepository;
import ru.practicum.mainService.repository.EventRepository;
import ru.practicum.mainService.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Service
public class PrivateCommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public PrivateCommentService(CommentRepository commentRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public CommentDto create(CommentDto commentDto, Integer userId, Integer eventId, LocalDateTime time) {
        User author = getUserById(userId);
        Event event = getEventById(eventId);
        Comment createdComment = CommentMapper.fromCommentDto(commentDto, author, event, time);
        return  CommentMapper.toCommentDto(commentRepository.save(createdComment));
    }

    private User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " was not found"));
    }

    private Event getEventById(Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" + eventId + " was not found"));
    }
}
