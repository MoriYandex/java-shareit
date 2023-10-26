package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingOutDto add(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid BookingInDto bookingInDto) {
        return bookingService.add(bookingInDto, userId);
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingOutDto approve(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable(name = "bookingId") Integer bookingId, @RequestParam(name = "approved") Boolean approved) {
        return bookingService.approve(bookingId, approved, userId);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingOutDto get(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable(name = "bookingId") Integer bookingId) {
        return bookingService.get(bookingId, userId);
    }

    @GetMapping()
    public List<BookingOutDto> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllByUserId(userId, state);
    }

    @GetMapping(path = "/owner")
    public List<BookingOutDto> getAllForItems(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllForItems(userId, state);
    }
}
