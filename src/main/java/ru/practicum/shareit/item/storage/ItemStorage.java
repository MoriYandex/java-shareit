package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item add(Item item);

    Item get(Integer id);

    Item update(Item item);

    List<Item> getAllByUserId(Integer userId);

    List<Item> getAvailableByText(String text);

    void deleteItemRequests(Integer requestId);
}
