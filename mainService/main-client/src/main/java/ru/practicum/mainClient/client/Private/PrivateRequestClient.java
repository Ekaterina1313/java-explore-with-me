package ru.practicum.mainClient.client.Private;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.common.dto.ParticipationRequestDto;
import ru.practicum.mainClient.BaseClient;

@Service
public class PrivateRequestClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public PrivateRequestClient(@Value("${services.private}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(Integer userId, ParticipationRequestDto dto) {
        String path = "/" + userId + "/requests";
        return post(path, dto);
    }

    public ResponseEntity<Object> getAll(Integer userId) {
        return get("/" + userId + "/requests");
    }

    public ResponseEntity<Object> cancelRequest(Integer userId, Integer requestId) {
        String path = "/" + userId + "/requests/" + requestId + "/cancel";
        return patch(path);
    }
}