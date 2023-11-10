package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
@Slf4j
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> add(long userId, ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(long userId, long itemId, ItemDto itemDto) {
        log.info("Updating item {}, itemId={}, userId={}", itemDto, itemId, userId);
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> get(long userId, long itemId) {
        log.info("Get item itemId={}, userId={}", itemId, userId);
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllByOwnerId(long userId, Integer from, Integer size) {
        log.info("Get items by owner userId={}, from={}, size={}", userId, from, size);
        if (from == null && size == null)
            return get("", userId);
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> searchAvailableByText(String text, Integer from, Integer size) {
        log.info("Search items by text {}, from={}, size={}", text, from, size);
        Map<String, Object> parameters;
        if (from == null && size == null) {
            parameters = Map.of("text", text);
            return get("/search?text={text}", 0L, parameters);
        }
        parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", 0L, parameters);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentDto commentDto) {
        log.info("Creating comment {} to item itemId={}, userId={}", commentDto, itemId, userId);
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
