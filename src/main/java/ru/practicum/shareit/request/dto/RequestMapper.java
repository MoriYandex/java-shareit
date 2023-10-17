package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.storage.UserStorage;

@Component
@RequiredArgsConstructor
public class RequestMapper {
    private final UserStorage userStorage;

    public ItemRequestDto toDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestorId(request.getRequestor() != null ? request.getRequestor().getId() : null)
                .created(request.getCreated())
                .build();
    }

    public ItemRequest fromDto(ItemRequestDto requestDto) {
        return ItemRequest.builder()
                .id(requestDto.getId())
                .description(requestDto.getDescription())
                .requestor(requestDto.getRequestorId() != null ? userStorage.get(requestDto.getRequestorId()) : null)
                .created(requestDto.getCreated())
                .build();
    }

}
