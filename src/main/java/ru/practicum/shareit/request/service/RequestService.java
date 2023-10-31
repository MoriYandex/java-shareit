package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto add(RequestDto requestDto);

    RequestDto update(RequestDto requestDto, Integer requestId);

    RequestDto get(Integer requestId);

    void delete(Integer requestId);

    List<RequestDto> getAllByUserId(Integer userId);
}
