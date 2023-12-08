package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.common.model.EventCompilation;

import java.util.List;

public interface EventCompilationRepository extends JpaRepository<EventCompilation, Integer> {
    @Query("SELECT ec FROM EventCompilation ec WHERE ec.compilation.id IN :compilationIds")
    List<EventCompilation> findAllByCompilationIds(List<Integer> compilationIds);
}