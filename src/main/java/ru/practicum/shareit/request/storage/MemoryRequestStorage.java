package ru.practicum.shareit.request.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MemoryRequestStorage implements RequestStorage {
    private final Map<Integer, ItemRequest> requestMap = new HashMap<>();
    private Integer requestSequence = 0;

    @Override
    public ItemRequest add(ItemRequest itemRequest) {
        log.info("Добавление запроса");
        itemRequest.setId(++requestSequence);
        requestMap.put(requestSequence, itemRequest);
        return itemRequest;
    }

    @Override
    public ItemRequest update(ItemRequest itemRequest) {
        log.info("Редактирование запроса с идентификатором {}", itemRequest.getId());
        if (!requestMap.containsKey(itemRequest.getId())) {
            log.error("Запрос с идентификатором {} не найден!", itemRequest.getId());
            throw new NotFoundException(String.format("Запрос с идентификатором %d не найден!", itemRequest.getId()));
        }
        requestMap.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public ItemRequest get(Integer id) {
        return requestMap.get(id);
    }

    @Override
    public void delete(Integer id) {
        log.info("Удаление запроса с идентификатором {}", id);
        if (!requestMap.containsKey(id)) {
            log.error("Запрос с идентификатором {} не найден!", id);
            throw new NotFoundException(String.format("Запрос с идентификатором %d не найден!", id));
        }
        requestMap.remove(id);
    }

    @Override
    public List<ItemRequest> getAllByUserId(Integer userId) {
        log.info("Поиск запросов по идентификатору пользователя {}", userId);
        return requestMap.values().stream().filter(x -> x.getRequestor() != null && Objects.equals(x.getRequestor().getId(), userId)).collect(Collectors.toList());
    }
}
