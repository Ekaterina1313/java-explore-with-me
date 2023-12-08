package ru.practicum.mainClient.client.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.common.dto.CategoryDto;
import ru.practicum.mainClient.BaseClient;

@Service
public class AdminCategoryClient extends BaseClient {
    private static final String API_PREFIX = "/admin/categories";

    @Autowired
    public AdminCategoryClient(@Value("${services.admin}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(CategoryDto categoryDto) {
        return post("", categoryDto);
    }

    public ResponseEntity<Object> update(Integer catId, CategoryDto categoryDto) {
        String path = "/" + catId;
        return patch(path, categoryDto);
    }

    public ResponseEntity<Object> delete(Integer catId) {
        String path = "/" + catId;
        return delete(path);
    }
}