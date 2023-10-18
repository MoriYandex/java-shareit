package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.storage.RequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private static final String OWNER_NOT_FOUND_MESSAGE = "Не найден владелец вещи!";
    private static final String CHANGE_USER_MESSAGE = "Нельзя редактировать владельца вещи, вещь может быть отредактирована только её владельцем!";
    private static final String CHANGE_REQUEST_MESSAGE = "Нельзя редактировать запрос на создание вещи!";
    private final ItemMapper itemMapper;
    private final ItemStorage itemStorage;

    private final UserStorage userStorage;
    private final RequestStorage requestStorage;

    @Override
    public ItemDto add(ItemDto itemDto, Integer userId) {
        log.info("Добавление вещи");
        User owner = userStorage.get(userId);
        validateOwner(owner);
        ItemRequest request = requestStorage.get(itemDto.getRequestId());
        Item item = itemMapper.fromDto(itemDto, owner, request);
        return itemMapper.toDto(itemStorage.add(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Integer userId, Integer itemId) {
        log.info("Редактирование вещи по идентификатору {}", itemId);
        Item item = itemStorage.get(itemId);
        if (item == null) {
            log.error("Вещь с идентификатором {} не найдена!", itemId);
            throw new NotFoundException(String.format("Вещь с идентификатором %d не найдена!", itemId));
        }
        if (!Objects.equals(userId, item.getOwner().getId())) {
            log.error(CHANGE_USER_MESSAGE);
            throw new ForbiddenException(CHANGE_USER_MESSAGE);
        }
        if (itemDto.getRequestId() != null && (item.getRequest() == null || !Objects.equals(itemDto.getRequestId(), item.getRequest().getId()))) {
            log.error(CHANGE_REQUEST_MESSAGE);
            throw new ValidationException(CHANGE_REQUEST_MESSAGE);
        }
        if (!Strings.isBlank(itemDto.getName()))
            item.setName(itemDto.getName());
        if (!Strings.isBlank(itemDto.getDescription()))
            item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            item.setAvailable(itemDto.getAvailable());
        //При работе с памятью вызывать метод хранилища необязательно, но оставляю с расчётом на работу с БД
        return itemMapper.toDto(itemStorage.update(item));
    }

    @Override
    public ItemDto get(Integer itemId) {
        log.info("Поиск вещи по идентификатору {}", itemId);
        Item result = itemStorage.get(itemId);
        if (result == null) {
            log.error("Вещь с идентификатором {} не найдена!", itemId);
            throw new NotFoundException(String.format("Вещь с идентификатором %d не найдена!", itemId));
        }
        return itemMapper.toDto(result);
    }

    @Override
    public List<ItemDto> getAllByUserId(Integer userId) {
        log.info("Поиск вещей по пользователю с идентификатором {}", userId);
        return itemStorage.getAllByUserId(userId).stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAvailableByText(String text) {
        log.info("Поиск вещей по описанию с текстом {}", text);
        if (Strings.isBlank(text))
            return new ArrayList<>();
        return itemStorage.getAvailableByText(text).stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteItemRequests(Integer requestId) {
        itemStorage.deleteItemRequests(requestId);
    }

    private void validateOwner(User owner) {
        if (owner == null) {
            log.error(OWNER_NOT_FOUND_MESSAGE);
            throw new NotFoundException(OWNER_NOT_FOUND_MESSAGE);
        }
    }
}
