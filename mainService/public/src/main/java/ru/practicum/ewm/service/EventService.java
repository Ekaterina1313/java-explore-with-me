package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.common.dto.EventFullDto;
import ru.practicum.common.error.IncorrectParamException;
import ru.practicum.common.mapper.EndpointHitMapper;
import ru.practicum.common.mapper.EventMapper;
import ru.practicum.common.model.EndpointHit;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.States;
import ru.practicum.ewm.repository.EventRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final RestTemplate restTemplate;
    private static String app = "mainService/public";
    private final static String endpointUrl = "http://ewm-service:9090/hit";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public EventService(EventRepository eventRepository, RestTemplate restTemplate) {
        this.eventRepository = eventRepository;
        this.restTemplate = restTemplate;
    }

    public List<EventFullDto> getEvents(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                        String rangeEnd, Boolean onlyAvailable, String sort, int from, int size,
                                        String clientIp, String endpointPath) {
        sort = sort.toLowerCase();
        if (sort.equals("views")) {
            sort = "views";
        } else if (sort.equals("event_date")) {
            sort = "eventDate";
        } else {
            throw new IncorrectParamException("Поле sort должно принимать значение EVENT_DATE или VIEWS," +
                    " текущее значение sort = " + sort);
        }

        EndpointHit endpointHit = EndpointHitMapper.createEndpointHit(app, clientIp, endpointPath);
        restTemplate.postForObject(endpointUrl, endpointHit, String.class);

        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, sort));
        LocalDateTime now = LocalDateTime.now();
        Page<Event> events;
        if (onlyAvailable) {
            if (text != null && !text.isBlank() && categories != null && !categories.isEmpty() &&
                    rangeStart != null && rangeEnd != null) {
                events = eventRepository.filteredOnlyAvailable(text, categories,
                        LocalDateTime.parse(rangeStart, formatter), LocalDateTime.parse(rangeEnd, formatter), paid,
                        pageable);
            } else if (text != null && !text.isBlank() &&
                    rangeStart == null && rangeEnd == null && categories == null) {
                events = eventRepository.findAllWithTextOnlyAvailable(text, paid, now, pageable);
            } else if (categories != null && !categories.isEmpty() &&
                    text == null && rangeStart == null && rangeEnd == null) {
                events = eventRepository.findAllWithCategoriesOnlyAvailable(categories, paid, now, pageable);
            } else if (rangeStart != null && rangeEnd != null &&
                    categories == null && text == null) {
                events = eventRepository.findAllWithStartAndEndOnlyAvailable(LocalDateTime.parse(rangeStart, formatter),
                        LocalDateTime.parse(rangeEnd, formatter), paid, pageable);
            } else {
                events = eventRepository.findAllOnlyAvailable(paid, now, pageable);
            }
        } else {
            if (text != null && !text.isBlank() && categories != null && !categories.isEmpty() &&
                    rangeStart != null && rangeEnd != null) {
                events = eventRepository.filteredNotAvailable(text, categories,
                        LocalDateTime.parse(rangeStart, formatter), LocalDateTime.parse(rangeEnd, formatter), paid,
                        pageable);
            } else if (text != null && !text.isBlank() &&
                    rangeEnd == null && rangeStart == null && categories == null) {
                events = eventRepository.findAllWithTextNotAvailable(text, paid, now, pageable);
            } else if (categories != null && !categories.isEmpty() &&
                    text == null && rangeStart == null && rangeEnd == null) {
                events = eventRepository.findAllWithCategoriesNotAvailable(categories, paid, now, pageable);
            } else if (rangeStart != null && rangeEnd != null &&
                    categories == null && text == null) {
                events = eventRepository.findAllWithStartAndEndNotAvailable(LocalDateTime.parse(rangeStart, formatter),
                        LocalDateTime.parse(rangeEnd, formatter), paid, pageable);
            } else {
                events = eventRepository.findAllNotAvailable(paid, now, pageable);
            }
        }
        List<Integer> eventIds = events.getContent()
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        eventRepository.incrementViews(eventIds);
        return events.getContent()
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    public EventFullDto getById(Integer eventId, String clientIp, String endpointPath) {
        Event eventById = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" + eventId + " was not found"));
        if (!eventById.getState().equals(States.PUBLISHED)) {
            throw new EntityNotFoundException("Event with id=" + eventId + " is not published");
        }
        EndpointHit endpointHitDto = EndpointHitMapper.createEndpointHit(app, clientIp, endpointPath);
        restTemplate.postForObject(endpointUrl, endpointHitDto, String.class);
        eventRepository.incrementViews(List.of(eventId));
        return EventMapper.toEventFullDto(eventById);
    }
}