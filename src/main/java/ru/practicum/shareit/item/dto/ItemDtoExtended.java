package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
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
    private BookingDto nextBooking;
    private BookingDto lastBooking;
    private List<CommentDto> comments;

    public ItemDtoExtended(@JsonProperty("id") Integer id,
                           @JsonProperty("name") String name,
                           @JsonProperty("description") String description,
                           @JsonProperty("available") Boolean available,
                           @JsonProperty("ownerId") Integer ownerId,
                           @JsonProperty("requestId") Integer requestId,
                           @JsonProperty("nextBooking") BookingDto nextBooking,
                           @JsonProperty("lastBooking") BookingDto lastBooking,
                           @JsonProperty("comments") List<CommentDto> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerId = ownerId;
        this.requestId = requestId;
        this.nextBooking = nextBooking;
        this.lastBooking = lastBooking;
        this.comments = comments;
    }
}
