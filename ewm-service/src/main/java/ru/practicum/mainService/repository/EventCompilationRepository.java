package ru.practicum.mainService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainService.model.EventCompilation;

import java.util.List;

public interface EventCompilationRepository extends JpaRepository<EventCompilation, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM EventCompilation ec WHERE ec.compilation.id = :compId")
    void deleteByCompilationId(Integer compId);

    @Transactional
    @Modifying
    @Query("DELETE FROM EventCompilation ec WHERE ec.event.id IN :ids")
    void deleteByEventIds(List<Integer> ids);

    @Query("SELECT ec FROM EventCompilation ec WHERE ec.compilation.id IN :compilationIds")
    List<EventCompilation> findAllByCompilationIds(List<Integer> compilationIds);
}