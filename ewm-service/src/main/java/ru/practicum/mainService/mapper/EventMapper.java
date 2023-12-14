package ru.practicum.mainService.mapper;

import ru.practicum.mainService.GetFormatter;
import ru.practicum.mainService.dto.CommentShortDto;
import ru.practicum.mainService.dto.EventDto;
import ru.practicum.mainService.dto.EventFullDto;
import ru.practicum.mainService.dto.EventShortDto;
import ru.practicum.mainService.model.*;

import java.time.LocalDateTime;
import java.util.List;

public class EventMapper {

    public static EventFullDto toEventFullDto(Event event, List<CommentShortDto> comments) {
        return new EventFullDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn().format(GetFormatter.getFormatter()),
                event.getDescription(),
                event.getEventDate().format(GetFormatter.getFormatter()),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                new Location(event.getLocationLat(), event.getLocationLon()),
                event.getPaid(),
                event.getParticipantLimit(),
                (event.getPublishedOn() == null) ? null :
                        event.getPublishedOn().format(GetFormatter.getFormatter()),
                event.getRequestModeration(),
                event.getState().name(),
                event.getTitle(),
                event.getViews(),
                comments

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
                LocalDateTime.parse(eventDto.getEventDate(), GetFormatter.getFormatter()),
                eventDto.getLocation().getLat(),
                eventDto.getLocation().getLon(),
                initiator,
                createdOn,
                eventDto.getPaid(),
                eventDto.getParticipantLimit(),
                eventDto.getRequestModeration(),
                (eventDto.getPublishedOn() == null) ? null :
                        LocalDateTime.parse(eventDto.getPublishedOn(), GetFormatter.getFormatter()),
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
                event.getEventDate().format(GetFormatter.getFormatter()),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                event.getViews()
        );
    }
}