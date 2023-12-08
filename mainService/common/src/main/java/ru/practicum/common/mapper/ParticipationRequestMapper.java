package ru.practicum.common.mapper;

import ru.practicum.common.dto.ParticipationRequestDto;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.ParticipationRequest;
import ru.practicum.common.model.Status;
import ru.practicum.common.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParticipationRequestMapper {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public static ParticipationRequest toParticipationRequest(Integer id,
                                                              LocalDateTime created,
                                                              Event event, User requester, Status status) {
        return new ParticipationRequest(
                id,
                created,
                event,
                requester,
                status
        );
    }

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return new ParticipationRequestDto(
                participationRequest.getCreated().format(formatter),
                participationRequest.getEvent().getId(),
                participationRequest.getId(),
                participationRequest.getRequester().getId(),
                participationRequest.getStatus().name()
        );
    }
}