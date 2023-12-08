package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
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

    @Modifying
    @Transactional
    @Query("UPDATE Event e SET e.views = e.views + 1 WHERE e.id IN :eventIds")
    void incrementViews(@Param("eventIds") List<Integer> eventIds);
}