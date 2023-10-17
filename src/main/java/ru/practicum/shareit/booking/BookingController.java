package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingDto add(@RequestBody BookingDto bookingDto) {
        return bookingService.add(bookingDto);
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDto update(@PathVariable(name = "requestId") Integer bookingId, @RequestBody BookingDto bookingDto) {
        return bookingService.update(bookingDto, bookingId);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDto get(@PathVariable(name = "bookingId") Integer bookingId) {
        return bookingService.get(bookingId);
    }

    @DeleteMapping(path = "/{bookingId}")
    public void delete(@PathVariable(name = "bookingId") Integer bookingId) {
        bookingService.delete(bookingId);
    }
}
