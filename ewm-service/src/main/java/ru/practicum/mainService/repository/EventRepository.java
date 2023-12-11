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

    @Query("SELECT e FROM Event e " +
            "WHERE e.state IN :states " +
            "AND e.category.id IN :categoryIds " +
            "AND e.createdOn BETWEEN :start AND :end")
    Page<Event> getFilteredEventsWithoutUsers(
            @Param("states") List<States> states,
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.initiator.id IN :userIds " +
            "AND e.state IN :states " +
            "AND e.createdOn BETWEEN :start AND :end")
    Page<Event> getFilteredEventsWithoutCategories(
            @Param("userIds") List<Integer> userIds,
            @Param("states") List<States> states,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state IN :states " +
            "AND e.createdOn BETWEEN :start AND :end")
    Page<Event> getFilteredEventsWithoutUsersAndCategories(
            @Param("states") List<States> states,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

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
            "AND e.category.id IN :categories " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND e.paid = :paid")
    Page<Event> filteredWithoutTextOnlyAvailable(
            @Param("categories") List<Integer> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("paid") Boolean paid,
            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND e.paid = :paid")
    Page<Event> filteredWithoutCategoriesOnlyAvailable(
            @Param("text") String text,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("paid") Boolean paid,
            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND e.paid = :paid")
    Page<Event> filteredWithoutTextAndCategoryOnlyAvailable(
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("paid") Boolean paid,
            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND e.participantLimit = e.confirmedRequests " +
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
            "AND e.participantLimit = e.confirmedRequests " +
            "AND e.category.id IN :categories " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND e.paid = :paid")
    Page<Event> filteredWithoutTextNotAvailable(
            @Param("categories") List<Integer> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("paid") Boolean paid,
            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND e.participantLimit = e.confirmedRequests " +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND e.paid = :paid")
    Page<Event> filteredWithoutCategoriesNotAvailable(
            @Param("text") String text,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("paid") Boolean paid,
            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND e.participantLimit = e.confirmedRequests " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND e.paid = :paid")
    Page<Event> filteredWithoutTextAndCategoryNotAvailable(
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("paid") Boolean paid,
            Pageable pageable);
}