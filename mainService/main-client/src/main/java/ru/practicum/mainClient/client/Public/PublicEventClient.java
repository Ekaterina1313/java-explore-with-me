package ru.practicum.mainClient.client.Public;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.mainClient.BaseClient;

import java.util.List;
import java.util.Optional;

@Service
public class PublicEventClient extends BaseClient {
    private static final String API_PREFIX = "/events";

    @Autowired
    public PublicEventClient(@Value("${services.public}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getEvents(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                            String rangeEnd, Boolean onlyAvailable, String sort, int from, int size,
                                            HttpHeaders headers) {
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        String path = "";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(path)
                .queryParamIfPresent("text", Optional.ofNullable(text))
                .queryParamIfPresent("categories", Optional.ofNullable(categories))
                .queryParam("paid", paid)
                .queryParamIfPresent("rangeStart", Optional.ofNullable(rangeStart))
                .queryParamIfPresent("rangeEnd", Optional.ofNullable(rangeEnd))
                .queryParam("onlyAvailable", onlyAvailable)
                .queryParam("sort", sort)
                .queryParam("from", from)
                .queryParam("size", size);
        String finalPath = uriBuilder.build().toUriString();
        try {
            return rest.exchange(finalPath, HttpMethod.GET, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
    }

    public ResponseEntity<Object> getById(Integer id, HttpHeaders headers) {
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        String path = "/" + id;
        try {
            return rest.exchange(path, HttpMethod.GET, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
    }
}