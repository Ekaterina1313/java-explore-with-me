package ru.practicum.mainService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainService.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

}
