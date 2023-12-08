package ru.practicum.mainClient.client.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.common.dto.NewCompilationDto;
import ru.practicum.mainClient.BaseClient;

@Service
public class AdminCompilationClient extends BaseClient {
    private static final String API_PREFIX = "/admin/compilations";

    @Autowired
    public AdminCompilationClient(@Value("${services.admin}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(NewCompilationDto NewCompilationDto) {
        return post("", NewCompilationDto);
    }

    public ResponseEntity<Object> deleteById(Integer compId) {
        String path = "/" + compId;
        return delete(path);
    }

    public ResponseEntity<Object> update(Integer compId, NewCompilationDto newCompilationDto) {
        String path = "/" + compId;
        return patch(path, newCompilationDto);
    }
}