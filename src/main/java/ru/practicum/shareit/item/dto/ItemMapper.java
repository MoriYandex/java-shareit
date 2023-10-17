package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.storage.RequestStorage;
import ru.practicum.shareit.user.storage.UserStorage;

@RequiredArgsConstructor
@Component
public class ItemMapper {
    private final UserStorage userStorage;
    private final RequestStorage requestStorage;

    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public Item fromDto(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwnerId() != null ? userStorage.get(itemDto.getOwnerId()) : null)
                .request(itemDto.getRequestId() != null ? requestStorage.get(itemDto.getRequestId()) : null)
                .build();
    }
}
