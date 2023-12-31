package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExtended;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, Integer userId);

    ItemDto update(ItemDto itemDto, Integer userId, Integer itemId);

    ItemDtoExtended get(Integer itemId, Integer userId);

    List<ItemDtoExtended> getAllByUserExtended(Integer userId, Integer from, Integer size);

    List<ItemDto> getAvailableByText(String text, Integer from, Integer size);
}
