package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.requests.dto.RequestDto;

import java.util.Map;

@Service
@Slf4j
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> add(long userId, RequestDto requestDto) {
        log.info("Creating request {}, userId={}", requestDto, userId);
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getById(long userId, long requestId) {
        log.info("Get request requestId={}, userId={}", requestId, userId);
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getAllByUserId(long userId) {
        log.info("Get requests by userId={}", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> getAll(long userId, Integer from, Integer size) {
        log.info("Get all requests from={}, size={}", from, size);
        if (from == null && size == null)
            return get("/all", userId);
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("/all?from={from}&size={size}", userId, parameters);
    }
}
