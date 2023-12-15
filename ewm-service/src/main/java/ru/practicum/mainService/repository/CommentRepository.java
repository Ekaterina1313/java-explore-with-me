package ru.practicum.mainService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainService.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("SELECT c FROM Comment c WHERE c.event.id IN :ids")
    List<Comment> findAllByEventIds(List<Integer> ids);

    @Query("SELECT c FROM Comment c WHERE c.author.id IN :ids")
    List<Comment> findAllByAuthorId(List<Integer> ids);
}