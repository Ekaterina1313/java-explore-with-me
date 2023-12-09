package ru.practicum.mainService.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainService.model.Event;
import ru.practicum.mainService.model.States;

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

    Page<Event> findEventsByInitiatorId(Integer userId, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND e.category.id IN :categories " +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND e.paid = :paid")
    Page<Event> filteredOnlyAvailable(
            @Param("text") String text,
            @Param("categories") List<Integer> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("paid") Boolean paid,
            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND e.eventDate > :now " +
            "AND e.paid = :paid")
    Page<Event> findAllOnlyAvailable(@Param("paid") Boolean paid,
                                     @Param("now") LocalDateTime now,
                                     Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND e.eventDate > :now " +
            "AND e.paid = :paid")
    Page<Event> findAllWithTextOnlyAvailable(@Param("text") String text,
                                             @Param("paid") Boolean paid,
                                             @Param("now") LocalDateTime now,
                                             Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND e.category.id IN :categories " +
            "AND e.eventDate > :now " +
            "AND e.paid = :paid")
    Page<Event> findAllWithCategoriesOnlyAvailable(@Param("categories") List<Integer> categories,
                                                   @Param("paid") Boolean paid,
                                                   @Param("now") LocalDateTime now,
                                                   Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND e.paid = :paid")
    Page<Event> findAllWithStartAndEndOnlyAvailable(@Param("rangeStart") LocalDateTime rangeStart,
                                                    @Param("rangeEnd") LocalDateTime rangeEnd,
                                                    @Param("paid") Boolean paid,
                                                    Pageable pageable);
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND e.category.id IN :categories " +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND e.paid = :paid")
    Page<Event> filteredNotAvailable(
            @Param("text") String text,
            @Param("categories") List<Integer> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("paid") Boolean paid,
            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND e.eventDate > :now " +
            "AND e.paid = :paid")
    Page<Event> findAllNotAvailable(@Param("paid") Boolean paid,
                                    @Param("now") LocalDateTime now,
                                    Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND e.eventDate > :now " +
            "AND e.paid = :paid")
    Page<Event> findAllWithTextNotAvailable(@Param("text") String text,
                                            @Param("paid") Boolean paid,
                                            @Param("now") LocalDateTime now,
                                            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND e.category.id IN :categories " +
            "AND e.eventDate > :now " +
            "AND e.paid = :paid")
    Page<Event> findAllWithCategoriesNotAvailable(@Param("categories") List<Integer> categories,
                                                  @Param("paid") Boolean paid,
                                                  @Param("now") LocalDateTime now,
                                                  Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND e.paid = :paid")
    Page<Event> findAllWithStartAndEndNotAvailable(@Param("rangeStart") LocalDateTime rangeStart,
                                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                                   @Param("paid") Boolean paid,
                                                   Pageable pageable);

}