package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoExtended;

import java.util.List;

public interface RequestService {
    RequestDto add(RequestDto requestDto, Integer userId);

    RequestDtoExtended getById(Integer requestId, Integer userId);

    List<RequestDtoExtended> getAllByUserId(Integer userId);

    List<RequestDtoExtended> getAll(Integer userId, Integer from, Integer size);
}
