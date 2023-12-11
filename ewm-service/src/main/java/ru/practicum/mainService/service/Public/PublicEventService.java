package ru.practicum.mainService.service.Public;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.mainService.dto.EventFullDto;
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
    //private static final String endpointUrl = "http://stats-server:9090/hit";
    private static final String endpointUrl = "http://stats-server:9090/hit";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public PublicEventService(EventRepository eventRepository, RestTemplate restTemplate) {
        this.eventRepository = eventRepository;
        this.restTemplate = restTemplate;
    }

    public List<EventFullDto> getEvents(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                        String rangeEnd, Boolean onlyAvailable, String sort, int from, int size,
                                        String clientIp, String endpointPath) {
        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, sort));
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

        EndpointHit endpointHit = EndpointHitMapper.createEndpointHit(app, clientIp, endpointPath);
        restTemplate.postForObject(endpointUrl, endpointHit, String.class);

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
        String path = "http://stats-server:9090/endpointHits?uri=" + endpointPath + "&clientIp=" + clientIp;
        ParameterizedTypeReference<List<EndpointHit>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<EndpointHit>> response = restTemplate.exchange(path, HttpMethod.GET,
                null, responseType);
        if (Objects.requireNonNull(response.getBody()).isEmpty()) {
            eventRepository.incrementViews(List.of(eventId));
        }

        EndpointHit endpointHitDto = EndpointHitMapper.createEndpointHit(app, clientIp, endpointPath);
        restTemplate.postForObject(endpointUrl, endpointHitDto, String.class);
        return EventMapper.toEventFullDto(eventById);
    }
}