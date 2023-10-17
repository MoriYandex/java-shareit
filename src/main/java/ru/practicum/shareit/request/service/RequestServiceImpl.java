package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.storage.RequestStorage;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestStorage requestStorage;
    private final RequestMapper requestMapper;
    private final ItemService itemService;

    @Override
    public ItemRequestDto add(ItemRequestDto requestDto) {
        return requestMapper.toDto(requestStorage.add(requestMapper.fromDto(requestDto)));
    }

    @Override
    public ItemRequestDto update(ItemRequestDto requestDto, Integer requestId) {
        requestDto.setId(requestId);
        return requestMapper.toDto(requestStorage.update(requestMapper.fromDto(requestDto)));
    }

    @Override
    public ItemRequestDto get(Integer requestId) {
        return requestMapper.toDto(requestStorage.get(requestId));
    }

    @Override
    public void delete(Integer requestId) {
        itemService.deleteItemRequests(requestId);
        requestStorage.delete(requestId);
    }

    @Override
    public List<ItemRequestDto> getAllByUserId(Integer userId) {
        return requestStorage.getAllByUserId(userId).stream().map(requestMapper::toDto).collect(Collectors.toList());
    }
}
