package ru.practicum.mainClient.client.Private;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.common.dto.EventDto;
import ru.practicum.common.dto.UpdatedEventDto;
import ru.practicum.common.model.EventRequestStatusUpdateRequest;
import ru.practicum.mainClient.BaseClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class PrivateEventClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public PrivateEventClient(@Value("${services.private}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(EventDto eventDto, Integer userId) {
        return post("/" + userId + "/events", eventDto);
    }

    public ResponseEntity<Object> getEvents(Integer userId, int from, int size) {
        Map<String, Object> parameters = new HashMap<>();
        String path = "/" + userId + "/events";
        parameters.put("from", from);
        parameters.put("size", size);
        return get(path, parameters);
    }

    public ResponseEntity<Object> getById(Integer userId, Integer eventId) {
        String path = "/" + userId + "/events/" + eventId;
        return get(path);
    }

    public ResponseEntity<Object> update(Integer userId, Integer eventId, UpdatedEventDto updatedEvent) {
        String path = "/" + userId + "/events/" + eventId;
        return patch(path, updatedEvent);
    }

    public ResponseEntity<Object> getRequests(Integer userId, Integer eventId) {
        String path = "/" + userId + "/events/" + eventId + "/requests";
        return get(path);
    }

    public ResponseEntity<Object> updateRequests(Integer userId, Integer eventId,
                                                 EventRequestStatusUpdateRequest request) {
        String path = "/" + userId + "/events/" + eventId + "/requests";
        return patch(path, request);
    }
}