package ru.practicum.common.mapper;

import ru.practicum.common.dto.EventDto;
import ru.practicum.common.dto.EventFullDto;
import ru.practicum.common.dto.EventShortDto;
import ru.practicum.common.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn().format(formatter),
                event.getDescription(),
                event.getEventDate().format(formatter),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                new Location(event.getLocationLat(), event.getLocationLon()),
                event.getPaid(),
                event.getParticipantLimit(),
                (event.getPublishedOn() == null) ? null :
                        event.getPublishedOn().format(formatter),
                event.getRequestModeration(),
                event.getState().name(),
                event.getTitle(),
                event.getViews()
        );
    }

    public static Event fromEventDto(EventDto eventDto, Category category, User initiator, Integer confirmedRequests,
                                     LocalDateTime createdOn, Integer views) {
        return new Event(
                eventDto.getId(),
                eventDto.getTitle(),
                eventDto.getAnnotation(),
                category,
                eventDto.getDescription(),
                LocalDateTime.parse(eventDto.getEventDate(), formatter),
                eventDto.getLocation().getLat(),
                eventDto.getLocation().getLon(),
                initiator,
                createdOn,
                eventDto.getPaid(),
                eventDto.getParticipantLimit(),
                eventDto.getRequestModeration(),
                (eventDto.getPublishedOn() == null) ? null :
                        LocalDateTime.parse(eventDto.getPublishedOn(), formatter),
                States.PENDING,
                confirmedRequests,
                views
        );
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate().format(formatter),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                event.getViews()
        );
    }
}