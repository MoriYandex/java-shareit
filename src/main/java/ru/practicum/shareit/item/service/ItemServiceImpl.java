package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemStorage itemStorage;

    @Override
    public ItemDto add(ItemDto itemDto, Integer userId) {
        itemDto.setOwnerId(userId);
        Item item = itemMapper.fromDto(itemDto);
        return itemMapper.toDto(itemStorage.add(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Integer userId, Integer itemId) {
        itemDto.setId(itemId);
        itemDto.setOwnerId(userId);
        return itemMapper.toDto(itemStorage.update(itemMapper.fromDto(itemDto)));
    }

    @Override
    public ItemDto get(Integer itemId) {
        return itemMapper.toDto(itemStorage.get(itemId));
    }

    @Override
    public List<ItemDto> getAllByUserId(Integer userId) {
        return itemStorage.getAllByUserId(userId).stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAvailableByText(String text) {
        return itemStorage.getAvailableByText(text).stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteItemRequests(Integer requestId) {
        itemStorage.deleteItemRequests(requestId);
    }
}
