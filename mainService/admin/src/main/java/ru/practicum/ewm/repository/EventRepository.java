package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.States;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query("SELECT e FROM Event e " +
            "WHERE e.initiator.id IN :userIds " +
            "AND e.state IN :states " +
            "AND e.category.id IN :categoryIds " +
            "AND e.createdOn BETWEEN :start AND :end")
    Page<Event> getFilteredEvents(
            @Param("userIds") List<Integer> userIds,
            @Param("states") List<States> states,
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.initiator.id IN :userIds")
    Page<Event> findAllByInitiatorIdIn(List<Integer> userIds, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.state IN :states")
    Page<Event> findAllByStateIn(List<States> states, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.category.id IN :categoryIds")
    Page<Event> findAllByCategoryIdIn(List<Integer> categoryIds, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.createdOn BETWEEN :start AND :end")
    Page<Event> findAllByCreatedOnBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.createdOn > :time")
    Page<Event> findAllByCreatedOnAfter(LocalDateTime time, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.createdOn < :time")
    Page<Event> findAllByCreatedOnBefore(LocalDateTime time, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Event e SET e.views = e.views + 1 WHERE e.id IN :eventIds")
    void incrementViews(@Param("eventIds") List<Integer> eventIds);
}