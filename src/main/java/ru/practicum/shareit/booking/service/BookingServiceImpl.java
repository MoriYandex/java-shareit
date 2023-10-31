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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingStatusDto.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    private final Map<BookingStatusDto, Function<User, List<BookingOutDto>>> userHandlerMap = Map.of(
            ALL, this::handleAllByUser,
            CURRENT, this::handleCurrentByUser,
            PAST, this::handlePastByUser,
            FUTURE, this::handleFutureByUser,
            WAITING, this::handleWaitingByUser,
            REJECTED, this::handleRejectedByUser);
    private final Map<BookingStatusDto, Function<User, List<BookingOutDto>>> itemHandlerMap = Map.of(
            ALL, this::handleAllByItem,
            CURRENT, this::handleCurrentByItem,
            PAST, this::handlePastByItem,
            FUTURE, this::handleFutureByItem,
            WAITING, this::handleWaitingByItem,
            REJECTED, this::handleRejectedByItem);

    @Override
    public BookingOutDto add(BookingInDto bookingInDto, Integer userId) {
        log.info("Создание бронирования для пользователя {} на вещь {}", userId, bookingInDto.getItemId());
        User booker = userRepository.findById(userId).orElse(null);
        if (booker == null) {
            log.error("Не найден пользователь {} для бронирования!", userId);
            throw new NotFoundException(String.format("Не найден пользователь %d для бронирования!", userId));
        }
        Item item = itemRepository.findById(bookingInDto.getItemId()).orElse(null);
        if (item == null) {
            log.error("Не найдена вещь с идентификатором {} для бронирования!", bookingInDto.getItemId());
            throw new NotFoundException(String.format("Не найдена вещь с идентификатором %d для бронирования!", bookingInDto.getItemId()));
        }
        if (!item.getAvailable()) {
            log.error("Вещь с идентификатором {} недоступна к бронированию!", item.getId());
            throw new ValidationException(String.format("Вещь с идентификатором %d недоступна к бронированию!", item.getId()));
        }
        if (userId.equals(item.getOwner().getId())) {
            log.error("Попытка пользователя {} забронировать собственную вещь {}", userId, item.getId());
            throw new NotFoundException(String.format("Попытка пользователя %d забронировать собственную вещь %d", userId, item.getId()));
        }
        bookingInDto.setBookerId(userId);
        validateTime(bookingInDto);
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
            log.error("Не найден пользователь {} для бронирования!", userId);
            throw new NotFoundException(String.format("Не найден пользователь %d для бронирования!", userId));
        }
        return userHandlerMap.get(bookingStatusDto).apply(booker);
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
            log.error("Не найден пользователь {} для поиска бронирований по вещам!", userId);
            throw new NotFoundException(String.format("Не найден пользователь %d для поиска бронирований по вещам!", userId));
        }
        return itemHandlerMap.get(bookingStatusDto).apply(owner);
    }

    private void validateTime(BookingInDto bookingInDto) {
        LocalDateTime start = bookingInDto.getStart();
        LocalDateTime end = bookingInDto.getEnd();
        LocalDateTime now = LocalDateTime.now();
        if (start.isBefore(now) || end.isBefore(now) || !start.isBefore(end)) {
            log.error("Неверно заданы параметры времени для бронирования вещи {} пользователем {}", bookingInDto.getItemId(), bookingInDto.getBookerId());
            throw new ValidationException(String.format("Неверно заданы параметры времени для бронирования вещи %d пользователем %d", bookingInDto.getItemId(), bookingInDto.getBookerId()));
        }
    }

    private List<BookingOutDto> handleAllByUser(User booker) {
        return bookingRepository.findAllByBookerOrderByStartDesc(booker).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
    }

    private List<BookingOutDto> handleCurrentByUser(User booker) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(booker, now, now).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
    }

    private List<BookingOutDto> handlePastByUser(User booker) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findAllByBookerAndEndIsBeforeOrderByStartDesc(booker, now).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
    }

    private List<BookingOutDto> handleFutureByUser(User booker) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findAllByBookerAndStartIsAfterOrderByStartDesc(booker, now).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
    }

    private List<BookingOutDto> handleWaitingByUser(User booker) {
        return bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.WAITING).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
    }

    private List<BookingOutDto> handleRejectedByUser(User booker) {
        return bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.REJECTED).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
    }

    private List<BookingOutDto> handleAllByItem(User owner) {
        return bookingRepository.findAllByItemOwner(owner).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
    }

    private List<BookingOutDto> handleCurrentByItem(User owner) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findAllByItemOwnerCurrent(owner, now).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
    }

    private List<BookingOutDto> handlePastByItem(User owner) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findAllByItemOwnerPast(owner, now).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
    }

    private List<BookingOutDto> handleFutureByItem(User owner) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findAllByItemOwnerFuture(owner, now).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
    }

    private List<BookingOutDto> handleWaitingByItem(User owner) {
        return bookingRepository.findAllByItemOwnerByStatus(owner, BookingStatus.WAITING).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
    }

    private List<BookingOutDto> handleRejectedByItem(User owner) {
        return bookingRepository.findAllByItemOwnerByStatus(owner, BookingStatus.REJECTED).stream().map(bookingMapper::toOutDto).collect(Collectors.toList());
    }
}
