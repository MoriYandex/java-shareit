package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
public class BookingMapper {
    public BookingOutDto toOutDto(Booking booking) {
        return BookingOutDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public BookingInDto toInDto(Booking booking) {
        return BookingInDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public Booking fromDto(BookingInDto bookingInDto, Item item, User booker) {
        return Booking.builder()
                .id(bookingInDto.getId())
                .start(bookingInDto.getStart())
                .end(bookingInDto.getEnd())
                .item(item)
                .booker(booker)
                .status(bookingInDto.getStatus())
                .build();
    }
}
