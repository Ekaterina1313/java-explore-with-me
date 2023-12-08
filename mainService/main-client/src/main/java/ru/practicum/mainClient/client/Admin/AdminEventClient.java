package ru.practicum.mainClient.client.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.common.dto.UpdatedEventDto;
import ru.practicum.mainClient.BaseClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminEventClient extends BaseClient {
    private static final String API_PREFIX = "/admin/events";

    @Autowired
    public AdminEventClient(@Value("${services.admin}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getEvents(List<Integer> users, List<String> states, List<Integer> categories,
                                            String rangeStart, String rangeEnd, int from, int size) {
        Map<String, Object> parameters = new HashMap<>();
        if (users != null) {
            parameters.put("users", users);
        }
        if (states != null) {
            parameters.put("states", states);
        }
        if (categories != null) {
            parameters.put("categories", categories);
        }
        if (rangeStart != null) {
            parameters.put("rangeStart", rangeStart);
        }
        if (rangeEnd != null) {
            parameters.put("rangeEnd", rangeEnd);
        }
        parameters.put("from", from);
        parameters.put("size", size);
        return get("", parameters);
    }

    public ResponseEntity<Object> update(Integer eventId, UpdatedEventDto updatedEvent) {
        String path = "/" + eventId;
        return patch(path, updatedEvent);
    }
}