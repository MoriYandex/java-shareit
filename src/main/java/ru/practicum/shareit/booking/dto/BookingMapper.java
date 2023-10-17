package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem() != null ? booking.getItem().getId() : null)
                .bookerId(booking.getBooker() != null ? booking.getBooker().getId() : null)
                .status(booking.getStatus())
                .build();
    }

    public Booking fromDto(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(bookingDto.getItemId() != null ? itemStorage.get(bookingDto.getItemId()) : null)
                .booker(bookingDto.getBookerId() != null ? userStorage.get(bookingDto.getBookerId()) : null)
                .status(bookingDto.getStatus())
                .build();
    }
}
