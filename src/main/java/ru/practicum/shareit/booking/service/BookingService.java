package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto add(BookingDto bookingDto);

    BookingDto update(BookingDto bookingDto, Integer bookingId);

    BookingDto get(Integer bookingId);

    void delete(Integer bookingId);

    List<BookingDto> getAllByUserId(Integer userId);
}
