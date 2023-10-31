package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

public interface BookingService {
    BookingOutDto add(BookingInDto bookingInDto, Integer userId);

    BookingOutDto get(Integer bookingId, Integer userId);

    List<BookingOutDto> getAllByUserId(Integer userId, String state);

    BookingOutDto approve(Integer bookingId, Boolean approved, Integer userId);

    List<BookingOutDto> getAllForItems(Integer userId, String state);
}
