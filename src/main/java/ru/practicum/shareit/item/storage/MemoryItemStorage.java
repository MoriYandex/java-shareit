package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MemoryItemStorage implements ItemStorage {
    private final static String OWNER_NOT_FOUND_MESSAGE = "Не найден владелец вещи!";
    private final static String EMPTY_NAME_MESSAGE = "Наименование вещи не может быть пустым!";
    private final static String EMPTY_DESCRIPTION_MESSAGE = "Описание вещи не может быть пустым!";
    private final static String EMPTY_AVAILABLE_MESSAGE = "Состояние доступности вещи не может быть пустым!";
    private final static String CHANGE_USER_MESSAGE = "Нельзя редактировать владельца вещи, вещь может быть отредактирована только её владельцем!";
    private final static String CHANGE_REQUEST_MESSAGE = "Нельзя редактировать запрос на создание вещи!";
    private final Map<Integer, Item> itemsMap = new HashMap<>();
    private Integer idSequence = 0;

    @Override
    public Item add(Item item) {
        log.info("Добавление вещи");
        validateItem(item);
        item.setId(++idSequence);
        itemsMap.put(idSequence, item);
        return item;
    }

    @Override
    public Item get(Integer id) {
        log.info("Поиск вещи по идентификатору {}", id);
        if (!itemsMap.containsKey(id)) {
            log.error("Вещь с идентификатором {} не найдена!", id);
            throw new NotFoundException(String.format("Вещь с идентификатором %d не найдена!", id));
        }
        return itemsMap.get(id);
    }

    @Override
    public Item update(Item item) {
        log.info("Редактирование вещи по идентификатору {}", item.getId());
        if (!itemsMap.containsKey(item.getId()))
            throw new NotFoundException(String.format("Вещь с идентификатором %d не найдена!", item.getId()));
        Item oldItem = itemsMap.get(item.getId());
        Item newItem = new Item(oldItem);
        if (!Strings.isBlank(item.getName()))
            newItem.setName(item.getName());
        if (!Strings.isBlank(item.getDescription()))
            newItem.setDescription(item.getDescription());
        if (item.getAvailable() != null)
            newItem.setAvailable(item.getAvailable());
        if (item.getOwner() != null && !Objects.equals(item.getOwner().getId(), newItem.getOwner().getId())) {
            log.error(CHANGE_USER_MESSAGE);
            throw new ForbiddenException(CHANGE_USER_MESSAGE);
        }
        if (item.getRequest() != null && (newItem.getRequest() == null || !Objects.equals(newItem.getRequest().getId(), item.getRequest().getId()))) {
            log.error(CHANGE_REQUEST_MESSAGE);
            throw new ValidationException(CHANGE_REQUEST_MESSAGE);
        }
        validateItem(newItem);
        itemsMap.put(item.getId(), newItem);
        return newItem;
    }

    @Override
    public List<Item> getAllByUserId(Integer userId) {
        log.info("Поиск вещей по пользователю с идентификатором {}", userId);
        return itemsMap.values().stream().filter(x -> Objects.equals(x.getOwner().getId(), userId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> getAvailableByText(String text) {
        log.info("Поиск вещей по описанию с текстом {}", text);
        if (Strings.isBlank(text))
            return new ArrayList<>();
        return itemsMap.values().stream().filter(x -> x.getDescription().toLowerCase().contains(text.toLowerCase()) && x.getAvailable()).collect(Collectors.toList());
    }

    @Override
    public void deleteItemRequests(Integer requestId) {
        itemsMap.values().stream().filter(x -> x.getRequest() != null && Objects.equals(x.getRequest().getId(), requestId)).forEach(x -> x.setRequest(null));
    }

    private void validateItem(Item item) {
        if (item.getOwner() == null) {
            log.error(OWNER_NOT_FOUND_MESSAGE);
            throw new NotFoundException(OWNER_NOT_FOUND_MESSAGE);
        }
        if (Strings.isBlank(item.getName())) {
            log.error(EMPTY_NAME_MESSAGE);
            throw new ValidationException(EMPTY_NAME_MESSAGE);
        }
        if (Strings.isBlank(item.getDescription())) {
            log.error(EMPTY_DESCRIPTION_MESSAGE);
            throw new ValidationException(EMPTY_DESCRIPTION_MESSAGE);
        }
        if (item.getAvailable() == null) {
            log.error(EMPTY_AVAILABLE_MESSAGE);
            throw new ValidationException(EMPTY_AVAILABLE_MESSAGE);
        }
    }
}
