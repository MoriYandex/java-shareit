package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingStorage {
    Booking add(Booking booking);

    Booking update(Booking booking);

    Booking get(Integer id);

    void delete(Integer id);

    List<Booking> findAllByUserId(Integer userId);
}
