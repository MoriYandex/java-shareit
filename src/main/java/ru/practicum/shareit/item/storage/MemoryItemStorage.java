package ru.practicum.shareit.item.storage;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MemoryItemStorage implements ItemStorage {
    private final Map<Integer, Item> itemsMap = new HashMap<>();
    private Integer idSequence = 0;

    @Override
    public Item add(Item item) {
        item.setId(++idSequence);
        itemsMap.put(idSequence, item);
        return item;
    }

    @Override
    public Item get(Integer id) {
        return itemsMap.get(id);
    }

    @Override
    public Item update(Item item) {
        itemsMap.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getAllByUserId(Integer userId) {
        return itemsMap.values().stream().filter(x -> Objects.equals(x.getOwner().getId(), userId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> getAvailableByText(String text) {
        if (Strings.isBlank(text))
            return new ArrayList<>();
        return itemsMap.values().stream().filter(x -> x.getDescription().toLowerCase().contains(text.toLowerCase()) && x.getAvailable()).collect(Collectors.toList());
    }

    @Override
    public void deleteItemRequests(Integer requestId) {
        itemsMap.values().stream().filter(x -> x.getRequest() != null && Objects.equals(x.getRequest().getId(), requestId)).forEach(x -> x.setRequest(null));
    }
}
