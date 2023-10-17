package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingStorage;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto add(BookingDto bookingDto) {
        return bookingMapper.toDto(bookingStorage.add(bookingMapper.fromDto(bookingDto)));
    }

    @Override
    public BookingDto update(BookingDto bookingDto, Integer bookingId) {
        bookingDto.setId(bookingId);
        return bookingMapper.toDto(bookingStorage.update(bookingMapper.fromDto(bookingDto)));
    }

    @Override
    public BookingDto get(Integer bookingId) {
        return bookingMapper.toDto(bookingStorage.get(bookingId));
    }

    @Override
    public void delete(Integer bookingId) {
        bookingStorage.delete(bookingId);
    }

    @Override
    public List<BookingDto> getAllByUserId(Integer userId) {
        return bookingStorage.findAllByUserId(userId).stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }
}
