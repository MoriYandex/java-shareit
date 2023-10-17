package ru.practicum.shareit.booking.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MemoryBookingStorage implements BookingStorage {
    private final Map<Integer, Booking> bookingMap = new HashMap<>();
    private Integer bookingSequence = 0;

    @Override
    public Booking add(Booking booking) {
        log.info("Добавление бронирования");
        booking.setId(++bookingSequence);
        bookingMap.put(bookingSequence, booking);
        return booking;
    }

    @Override
    public Booking update(Booking booking) {
        log.info("Редактирование бронирования с идентификатором {}", booking.getId());
        if (!bookingMap.containsKey(booking.getId())) {
            log.error("Бронирование с идентификатором {} не найдено!", booking.getId());
            throw new NotFoundException(String.format("Бронирование с идентификатором %d не найдено!", booking.getId()));
        }
        bookingMap.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Booking get(Integer id) {
        log.info("Поиск бронирования по идентификатору {}", id);
        if (!bookingMap.containsKey(id)) {
            log.error("Бронирование с идентификатором {} не найдено!", id);
            throw new NotFoundException(String.format("Бронирование с идентификатором %d не найдено!", id));
        }
        return bookingMap.get(id);
    }

    @Override
    public void delete(Integer id) {
        log.info("Удаление бронирования по идентификатору {}", id);
        if (!bookingMap.containsKey(id)) {
            log.error("Бронирование с идентификатором {} не найдено!", id);
            throw new NotFoundException(String.format("Бронирование с идентификатором %d не найдено!", id));
        }
        bookingMap.remove(id);
    }

    @Override
    public List<Booking> findAllByUserId(Integer userId) {
        log.info("Поиск бронирований по идентификатору пользователя {}", userId);
        return bookingMap.values().stream().filter(x -> x.getBooker() != null && Objects.equals(x.getBooker().getId(), userId)).collect(Collectors.toList());
    }
}
