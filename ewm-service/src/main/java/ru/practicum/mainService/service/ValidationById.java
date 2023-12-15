package ru.practicum.mainService.service;

import org.springframework.stereotype.Component;
import ru.practicum.mainService.model.*;
import ru.practicum.mainService.repository.*;

import javax.persistence.EntityNotFoundException;

@Component
public class ValidationById {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;
    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;

    public ValidationById(UserRepository userRepository, EventRepository eventRepository,
                          CommentRepository commentRepository, CategoryRepository categoryRepository,
                          CompilationRepository compilationRepository, RequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.commentRepository = commentRepository;
        this.categoryRepository = categoryRepository;
        this.compilationRepository = compilationRepository;
        this.requestRepository = requestRepository;
    }

    public Category getCategoryById(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id=" + categoryId + " was not found"));
    }

    public Event getEventById(Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" + eventId + " was not found"));
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " was not found"));
    }

    public Comment getCommentById(Integer commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id=" + commentId + " was not found"));
    }

    public Compilation getCompilationById(Integer compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation with id=" + compilationId +
                        " was not found"));
    }

    public ParticipationRequest getParticipationRequestById(Integer requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request with id=" + requestId +
                        " was not found"));
    }
}