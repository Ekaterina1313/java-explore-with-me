package ru.practicum.mainClient.client.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.common.dto.UserDto;
import ru.practicum.mainClient.BaseClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminUserClient extends BaseClient {
    private static final String API_PREFIX = "/admin/users";

    @Autowired
    public AdminUserClient(@Value("${services.admin}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> get(int from, int size, List<Integer> ids) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", from);
        parameters.put("size", size);
        parameters.put("ids", ids);
        return get("", parameters);
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> delete(Integer userId) {
        String path = "/" + userId;
        return delete(path);
    }
}