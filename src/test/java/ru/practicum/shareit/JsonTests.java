package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoExtended;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExtended;
import ru.practicum.shareit.request.dto.RequestDtoExtended;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class JsonTests {
    @Autowired
    private JacksonTester<BookingDtoExtended> bookingDtoExtendedJacksonTester;
    @Autowired
    private JacksonTester<ItemDtoExtended> itemDtoExtendedJacksonTester;
    @Autowired
    private JacksonTester<RequestDtoExtended> requestDtoExtendedJacksonTester;

    private final LocalDateTime bookingStart = LocalDateTime.of(2023, 1, 1, 1, 1, 1);
    private final LocalDateTime bookingEnd = LocalDateTime.of(2024, 2, 2, 2, 2, 2);

    @Test
    void bookingDtoExtendedTest() throws IOException {
        BookingDtoExtended bookingDtoExtended = new BookingDtoExtended(1,
                bookingStart,
                bookingEnd,
                new ItemDto(1, "name1", "description1", true, 1, null),
                new UserDto(1, "uname1", "user@user.com"),
                BookingStatus.APPROVED);
        JsonContent<BookingDtoExtended> jsonContent = bookingDtoExtendedJacksonTester.write(bookingDtoExtended);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-01T01:01:01");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        BookingDtoExtended newBookingDto = bookingDtoExtendedJacksonTester.parseObject(jsonContent.getJson());
        Assertions.assertEquals(newBookingDto.getId(), bookingDtoExtended.getId());
        Assertions.assertEquals(newBookingDto.getStart(), bookingDtoExtended.getStart());
        Assertions.assertEquals(newBookingDto.getBooker().getId(), bookingDtoExtended.getBooker().getId());
        Assertions.assertEquals(newBookingDto.getItem().getId(), bookingDtoExtended.getItem().getId());
        Assertions.assertEquals(newBookingDto.getStatus(), bookingDtoExtended.getStatus());
    }

    @Test
    void itemDtoExtendedTest() throws IOException {
        ItemDtoExtended itemDtoExtended = new ItemDtoExtended(1,
                "name1",
                "description1",
                true,
                1,
                null,
                new BookingDto(1, bookingStart, bookingEnd, 1, 2, BookingStatus.APPROVED),
                new BookingDto(1, bookingStart, bookingEnd, 1, 2, BookingStatus.APPROVED),
                List.of(new CommentDto(1, "text1", 1, 2, "name2", bookingEnd)));
        JsonContent<ItemDtoExtended> jsonContent = itemDtoExtendedJacksonTester.write(itemDtoExtended);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("description1");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("text1");
        ItemDtoExtended newItemDto = itemDtoExtendedJacksonTester.parseObject(jsonContent.getJson());
        Assertions.assertEquals(newItemDto.getId(), itemDtoExtended.getId());
        Assertions.assertEquals(newItemDto.getDescription(), itemDtoExtended.getDescription());
        Assertions.assertEquals(newItemDto.getNextBooking().getId(), itemDtoExtended.getNextBooking().getId());
        Assertions.assertEquals(newItemDto.getLastBooking().getId(), itemDtoExtended.getLastBooking().getId());
        Assertions.assertEquals(newItemDto.getComments().get(0).getId(), itemDtoExtended.getComments().get(0).getId());
    }

    @Test
    void requestDtoExtendedTest() throws IOException {
        RequestDtoExtended requestDtoExtended = new RequestDtoExtended(1,
                "description1",
                1,
                bookingStart,
                List.of(new ItemDto(1, "name1", "description1", true, 2, 1)));
        JsonContent<RequestDtoExtended> jsonContent = requestDtoExtendedJacksonTester.write(requestDtoExtended);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("description1");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created").isEqualTo("2023-01-01T01:01:01");
        assertThat(jsonContent).extractingJsonPathStringValue("$.items[0].name").isEqualTo("name1");
        RequestDtoExtended newRequestDto = requestDtoExtendedJacksonTester.parseObject(jsonContent.getJson());
        Assertions.assertEquals(newRequestDto.getId(), requestDtoExtended.getId());
        Assertions.assertEquals(newRequestDto.getDescription(), requestDtoExtended.getDescription());
        Assertions.assertEquals(newRequestDto.getCreated(), requestDtoExtended.getCreated());
        Assertions.assertEquals(newRequestDto.getItems().get(0).getId(), requestDtoExtended.getItems().get(0).getId());
    }
}
