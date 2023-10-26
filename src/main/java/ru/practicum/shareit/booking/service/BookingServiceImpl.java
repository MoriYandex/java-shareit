package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private static final String BOOKER_NOT_FOUND_MESSAGE = "Не найден создатель бронирования!";
    private static final String OWNER_NOT_FOUND_MESSAGE = "Не найден пользователь для поиска по вещам!";
    private static final String WRONG_TIME_EXCEPTION = "Неверно заданы параметры времени!";
    private static final String ITEM_NOT_FOUND_MESSAGE = "Не найдена вещь для бронирования!";
    private static final String ITEM_UNAVAILABLE_MESSAGE = "Вещь недоступна к бронированию!";
    private static final String OWN_ITEM_MESSAGE = "Нельзя бронировать собственную вещь!";

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingOutDto add(BookingInDto bookingInDto, Integer userId) {
        log.info("Создание бронирования для пользователя {} на вещь {}", userId, bookingInDto.getItemId());
        User booker = userRepository.findById(userId).orElse(null);
        if (booker == null) {
            log.error(BOOKER_NOT_FOUND_MESSAGE);
            throw new NotFoundException(BOOKER_NOT_FOUND_MESSAGE);
        }
        Item item = itemRepository.findById(bookingInDto.getItemId()).orElse(null);
        if (item == null) {
            log.error(ITEM_NOT_FOUND_MESSAGE);
            throw new NotFoundException(ITEM_NOT_FOUND_MESSAGE);
        }
        if (!item.getAvailable()) {
            log.error(ITEM_UNAVAILABLE_MESSAGE);
            throw new ValidationException(ITEM_UNAVAILABLE_MESSAGE);
        }
        if (userId.equals(item.getOwner().getId())) {
            log.error(OWN_ITEM_MESSAGE);
            throw new NotFoundException(OWN_ITEM_MESSAGE);
        }
        validateTime(bookingInDto.getStart(), bookingInDto.getEnd());
        Booking booking = bookingMapper.fromDto(bookingInDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);
        return bookingMapper.toOutDto(bookingRepository.saveAndFlush(booking));
    }

    @Override
    public BookingOutDto get(Integer bookingId, Integer userId) {
        log.info("Получение информации о бронировании {} пользователем {}", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            log.error("Не найдено бронирование по идентификатору {}", bookingId);
            throw new NotFoundException(String.format("Не найдено бронирование по идентификатору %d", bookingId));
        }
        if (!Objects.equals(booking.getBooker().getId(), userId) && !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            log.error("Вывод данных запрещён: пользователь с идентификатором {} не является автором бронирования либо владельцем вещи!", userId);
            throw new NotFoundException(String.format("Вывод данных запрещён: пользователь с идентификатором %d не является автором бронирования либо владельцем вещи!", userId));
        }
        return bookingMapper.toOutDto(booking);
    }

    @Override
    public List<BookingOutDto> getAllByUserId(Integer userId, String state) {
        log.info("Получение бронирований по идентификатору пользователя {} и статусу {}", userId, state);
        BookingStatusDto bookingStatusDto = getState(state);
        User booker = userRepository.findById(userId).orElse(null);
        if (booker == null) {
            log.error(BOOKER_NOT_FOUND_MESSAGE);
            throw new NotFoundException(BOOKER_NOT_FOUND_MESSAGE);
        }
        LocalDateTime now = LocalDateTime.now();
        switch (bookingStatusDto) {
            case ALL:
                return bookingRepository.findAllByBookerOrderByStartDesc(booker).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(booker, now, now).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByBookerAndEndIsBeforeOrderByStartDesc(booker, now).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBookerAndStartIsAfterOrderByStartDesc(booker, now).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.WAITING).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.REJECTED).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }

    private BookingStatusDto getState(String state) {
        try {
            return BookingStatusDto.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public BookingOutDto approve(Integer bookingId, Boolean approved, Integer userId) {
        log.info("Подтверждение бронирования по идентификатору {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            log.error("Не найдено бронирование по идентификатору {}", bookingId);
            throw new NotFoundException(String.format("Не найдено бронирование по идентификатору %d", bookingId));
        }
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            log.error("Подтверждение запрещено: пользователь с идентификатором {} не является владельцем вещи с идентификатором {}", userId, booking.getItem().getId());
            throw new NotFoundException(String.format("Подтверждение запрещено: пользователь с идентификатором %d не является владельцем вещи с идентификатором %d", userId, booking.getItem().getId()));
        }
        if (booking.getStatus() == BookingStatus.WAITING)
            booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        else {
            log.error("Попытка изменить статус бронирования {}, не находящегося в ожидании!", bookingId);
            throw new ValidationException(String.format("Попытка изменить статус бронирования %d, не находящегося в ожидании!", bookingId));
        }
        return bookingMapper.toOutDto(bookingRepository.saveAndFlush(booking));
    }

    @Override
    public List<BookingOutDto> getAllForItems(Integer userId, String state) {
        log.info("Получение бронирований по вещам пользователя {} и статусу {}", userId, state);
        BookingStatusDto bookingStatusDto = getState(state);
        User owner = userRepository.findById(userId).orElse(null);
        if (owner == null) {
            log.error(OWNER_NOT_FOUND_MESSAGE);
            throw new NotFoundException(OWNER_NOT_FOUND_MESSAGE);
        }
        LocalDateTime now = LocalDateTime.now();
        switch (bookingStatusDto) {
            case ALL:
                return bookingRepository.findAllByItemOwner(owner).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByItemOwnerCurrent(owner, now).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByItemOwnerPast(owner, now).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByItemOwnerFuture(owner, now).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByItemOwnerByStatus(owner, BookingStatus.WAITING).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByItemOwnerByStatus(owner, BookingStatus.REJECTED).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }

    private void validateTime(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();
        if (start.isBefore(now) || end.isBefore(now) || !start.isBefore(end)) {
            log.error(WRONG_TIME_EXCEPTION);
            throw new ValidationException(WRONG_TIME_EXCEPTION);
        }
    }
}
