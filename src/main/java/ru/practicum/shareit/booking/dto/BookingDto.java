package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Integer id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull
    private Integer itemId;
    private Integer bookerId;
    private BookingStatus status;

    public BookingDto(@JsonProperty("id") Integer id,
                      @JsonProperty("start") LocalDateTime start,
                      @JsonProperty("end") LocalDateTime end,
                      @JsonProperty("itemId") Integer itemId,
                      @JsonProperty("bookerId") Integer bookerId,
                      @JsonProperty("status") BookingStatus status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.itemId = itemId;
        this.bookerId = bookerId;
        this.status = status;
    }
}
