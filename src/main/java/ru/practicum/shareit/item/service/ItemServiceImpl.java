package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExtended;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto add(ItemDto itemDto, Integer userId) {
        log.info("Добавление вещи");
        User owner = userRepository.findById(userId).orElse(null);
        if (owner == null) {
            log.error(OWNER_NOT_FOUND_MESSAGE);
            throw new NotFoundException(OWNER_NOT_FOUND_MESSAGE);
        }
        Request request = itemDto.getRequestId() != null
                ? requestRepository.findById(itemDto.getRequestId()).orElse(null)
                : null;
        Item item = itemMapper.fromDto(itemDto, owner, request);
        return itemMapper.toDto(itemRepository.saveAndFlush(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Integer userId, Integer itemId) {
        log.info("Редактирование вещи по идентификатору {}", itemId);
        Item item = itemRepository.findById(itemId).orElse(null);
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
        return itemMapper.toDto(itemRepository.saveAndFlush(item));
    }

    @Override
    public ItemDtoExtended get(Integer itemId, Integer userId) {
        log.info("Поиск вещи по идентификатору {}", itemId);
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            log.error("Вещь с идентификатором {} не найдена!", itemId);
            throw new NotFoundException(String.format("Вещь с идентификатором %d не найдена!", itemId));
        }
        List<CommentDto> comments = commentRepository.getAllByItemOrderByCreatedDesc(item).stream().map(commentMapper::toDto).collect(Collectors.toList());
        if (!Objects.equals(item.getOwner().getId(), userId))
            return itemMapper.toDtoWithBooking(item, comments);
        List<Booking> bookingList = bookingRepository.findAllByItemOrderByStartDesc(item);
        LocalDateTime now = LocalDateTime.now();
        Booking nextBooking = bookingList.stream()
                .filter(x -> Objects.equals(x.getItem().getId(), item.getId()) && x.getStart().isAfter(now) && x.getStatus() == BookingStatus.APPROVED).min(Comparator.comparing(Booking::getStart)).orElse(null);
        Booking lastBooking = bookingList.stream()
                .filter(x -> Objects.equals(x.getItem().getId(), item.getId()) && x.getStart().isBefore(now) && x.getStatus() == BookingStatus.APPROVED).max(Comparator.comparing(Booking::getEnd)).orElse(null);
        return itemMapper.toDtoWithBooking(item,
                nextBooking != null ? bookingMapper.toInDto(nextBooking) : null,
                lastBooking != null ? bookingMapper.toInDto(lastBooking) : null,
                comments);
    }

    @Override
    public List<ItemDto> getAllByUserId(Integer userId) {
        User owner = userRepository.findById(userId).orElse(null);
        if (owner == null) {
            log.error(OWNER_NOT_FOUND_MESSAGE);
            throw new NotFoundException(OWNER_NOT_FOUND_MESSAGE);
        }
        return itemRepository.findAllByOwner(owner).stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDtoExtended> getAllByUserExtended(Integer userId) {
        log.info("Поиск вещей по пользователю с идентификатором {}", userId);
        User owner = userRepository.findById(userId).orElse(null);
        if (owner == null) {
            log.error(OWNER_NOT_FOUND_MESSAGE);
            throw new NotFoundException(OWNER_NOT_FOUND_MESSAGE);
        }
        List<Item> itemList = itemRepository.findAllByOwner(owner);
        List<Booking> bookingList = bookingRepository.findAllByItemOwner(owner);
        LocalDateTime now = LocalDateTime.now();
        List<Comment> commentList = commentRepository.getAllByOwner(owner);
        return itemList.stream().map(item -> {
            Booking nextBooking = bookingList.stream()
                    .filter(x -> Objects.equals(x.getItem().getId(), item.getId()) && x.getStart().isAfter(now) && x.getStatus() == BookingStatus.APPROVED).min(Comparator.comparing(Booking::getStart)).orElse(null);
            Booking lastBooking = bookingList.stream()
                    .filter(x -> Objects.equals(x.getItem().getId(), item.getId()) && x.getStart().isBefore(now) && x.getStatus() == BookingStatus.APPROVED).max(Comparator.comparing(Booking::getEnd)).orElse(null);
            List<CommentDto> comments = commentList.stream()
                    .filter(x -> Objects.equals(x.getItem().getId(), item.getId()))
                    .sorted(Comparator.comparing(Comment::getCreated).reversed())
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList());
            return itemMapper.toDtoWithBooking(item,
                    nextBooking != null ? bookingMapper.toInDto(nextBooking) : null,
                    lastBooking != null ? bookingMapper.toInDto(lastBooking) : null,
                    comments);
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAvailableByText(String text) {
        log.info("Поиск вещей по описанию с текстом {}", text);
        if (Strings.isBlank(text))
            return new ArrayList<>();
        return itemRepository.searchByText(true, text)
                .stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteItemRequests(Integer requestId) {
        requestRepository.deleteById(requestId);
    }
}
