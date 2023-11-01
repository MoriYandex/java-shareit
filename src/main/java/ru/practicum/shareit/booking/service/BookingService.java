package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoExtended;

import java.util.List;

public interface BookingService {
    BookingDtoExtended add(BookingDto bookingDto, Integer userId);

    BookingDtoExtended get(Integer bookingId, Integer userId);

    List<BookingDtoExtended> getAllByUserId(Integer userId, String state, Integer from, Integer size);

    BookingDtoExtended approve(Integer bookingId, Boolean approved, Integer userId);

    List<BookingDtoExtended> getAllForItems(Integer userId, String state, Integer from, Integer size);
}
