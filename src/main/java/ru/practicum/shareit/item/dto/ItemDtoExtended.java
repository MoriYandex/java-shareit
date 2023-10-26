package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

@Data
@Builder
public class ItemDtoExtended {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer ownerId;
    private Integer requestId;
    private BookingInDto nextBooking;
    private BookingInDto lastBooking;
    private List<CommentDto> comments;
}
