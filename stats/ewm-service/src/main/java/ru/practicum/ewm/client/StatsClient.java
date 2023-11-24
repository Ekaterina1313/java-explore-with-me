package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

import java.util.List;

@Component
public class StatsClient {
    @Value("${stats-server.url}")
    private String statApiUrl;

    private final RestTemplate restTemplate;

    public StatsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void saveEndpointHit(EndpointHit endpointHit) {
        String endpointUrl = statApiUrl + "/hit";
        restTemplate.postForObject(endpointUrl, endpointHit, Void.class);
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) {
        String uriString = uris != null ? String.join(", ", uris) : "";
        String endpointUrl = statApiUrl + "/stats?start={start}&end={end}&uris={uriString}&unique={unique}";

        ResponseEntity<List<ViewStats>> response = restTemplate.exchange(
                endpointUrl,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<ViewStats>>() {
                },
                start, end, uriString, unique
        );
        return response.getBody();
    }
}