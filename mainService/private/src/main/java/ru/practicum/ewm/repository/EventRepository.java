package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.common.model.Event;

public interface EventRepository extends JpaRepository<Event, Integer> {
    Page<Event> findEventsByInitiatorId(Integer userId, Pageable pageable);
}