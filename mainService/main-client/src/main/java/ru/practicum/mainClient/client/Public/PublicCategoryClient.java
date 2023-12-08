package ru.practicum.mainClient.client.Public;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.mainClient.BaseClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class PublicCategoryClient extends BaseClient {
    private static final String API_PREFIX = "/categories";

    @Autowired
    public PublicCategoryClient(@Value("${services.public}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAll(int from, int size) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", from);
        parameters.put("size", size);
        return get("", parameters);
    }

    public ResponseEntity<Object> getById(Integer catId) {
        String path = "/" + catId;
        return get(path);
    }
}