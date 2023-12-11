package ru.practicum.mainService.service.Public;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.mainService.dto.EventFullDto;
import ru.practicum.mainService.error.IncorrectParamException;
import ru.practicum.mainService.mapper.EndpointHitMapper;
import ru.practicum.mainService.mapper.EventMapper;
import ru.practicum.mainService.model.EndpointHit;
import ru.practicum.mainService.model.Event;
import ru.practicum.mainService.model.States;
import ru.practicum.mainService.repository.EventRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
public class PublicEventService {
    private final EventRepository eventRepository;
    private final RestTemplate restTemplate;
    private static final String app = "mainService/public";
    private static final String endpointUrl = "http://stats-server:9090/hit";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public PublicEventService(EventRepository eventRepository, RestTemplate restTemplate) {
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

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().format(formatter);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.of(999_999_998, 1, 1, 0, 0, 0)
                    .format(formatter);
        }
        
        EndpointHit endpointHit = EndpointHitMapper.createEndpointHit(app, clientIp, endpointPath);
        restTemplate.postForObject(endpointUrl, endpointHit, String.class);

        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, sort));
        LocalDateTime now = LocalDateTime.now();
        Page<Event> events;
        if (onlyAvailable) {
            if (Objects.equals(text, "0") && Objects.equals(categories.get(0), 0)) {
                events = eventRepository.filteredWithoutTextAndCategoryOnlyAvailable(LocalDateTime.parse(rangeStart,
                        formatter), LocalDateTime.parse(rangeEnd, formatter), paid, pageable);
            } else if (Objects.equals(text, "0")) {
                events = eventRepository.filteredWithoutTextOnlyAvailable(categories, LocalDateTime.parse(rangeStart,
                        formatter), LocalDateTime.parse(rangeEnd, formatter), paid, pageable);
            } else if (Objects.equals(categories.get(0), 0)) {
                events = eventRepository.filteredWithoutCategoriesOnlyAvailable(text, LocalDateTime.parse(rangeStart,
                        formatter), LocalDateTime.parse(rangeEnd, formatter), paid, pageable);
            } else {
                events = eventRepository.filteredOnlyAvailable(text, categories, LocalDateTime.parse(rangeStart,
                        formatter), LocalDateTime.parse(rangeEnd, formatter), paid, pageable);
            }

        } else {
            if (Objects.equals(text, "0") && Objects.equals(categories.get(0), 0)) {
                events = eventRepository.filteredWithoutTextAndCategoryNotAvailable(LocalDateTime.parse(rangeStart,
                        formatter), LocalDateTime.parse(rangeEnd, formatter), paid, pageable);
            } else if (Objects.equals(text, "0")) {
                events = eventRepository.filteredWithoutTextNotAvailable(categories, LocalDateTime.parse(rangeStart,
                        formatter), LocalDateTime.parse(rangeEnd, formatter), paid, pageable);
            } else if (Objects.equals(categories.get(0), 0)) {
                events = eventRepository.filteredWithoutCategoriesNotAvailable(text, LocalDateTime.parse(rangeStart,
                                formatter),
                        LocalDateTime.parse(rangeEnd, formatter), paid, pageable);
            } else {
                events = eventRepository.filteredNotAvailable(text, categories, LocalDateTime.parse(rangeStart,
                                formatter),
                        LocalDateTime.parse(rangeEnd, formatter), paid, pageable);
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