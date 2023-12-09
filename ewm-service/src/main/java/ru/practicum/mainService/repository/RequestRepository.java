package ru.practicum.mainService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainService.model.ParticipationRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Integer> {
    List<ParticipationRequest> findAllByRequesterId(Integer requesterId);

    List<ParticipationRequest> findAllByEventId(Integer eventId);
}