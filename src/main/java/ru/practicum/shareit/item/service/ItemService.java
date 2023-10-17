package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, Integer userId);

    ItemDto update(ItemDto itemDto, Integer userId, Integer itemId);

    ItemDto get(Integer itemId);

    List<ItemDto> getAllByUserId(Integer userId);

    List<ItemDto> getAvailableByText(String text);

    void deleteItemRequests(Integer requestId);
}
