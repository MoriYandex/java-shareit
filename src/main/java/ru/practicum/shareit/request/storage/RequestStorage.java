package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface RequestStorage {
    ItemRequest add(ItemRequest itemRequest);

    ItemRequest update(ItemRequest itemRequest);

    ItemRequest get(Integer id);

    void delete(Integer id);

    List<ItemRequest> getAllByUserId(Integer userId);
}
