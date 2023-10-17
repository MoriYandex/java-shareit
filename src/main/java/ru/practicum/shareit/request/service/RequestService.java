package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto add(ItemRequestDto requestDto);

    ItemRequestDto update(ItemRequestDto requestDto, Integer requestId);

    ItemRequestDto get(Integer requestId);

    void delete(Integer requestId);

    List<ItemRequestDto> getAllByUserId(Integer userId);
}
