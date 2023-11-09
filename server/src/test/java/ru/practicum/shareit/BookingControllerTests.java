package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests extends ControllerTests<BookingController> {
    @Autowired
    public BookingControllerTests(BookingController testController) {
        super(testController);
    }

    private final BookingMapper bookingMapper = new BookingMapper();
    private final ItemMapper itemMapper = new ItemMapper();
    private final UserMapper userMapper = new UserMapper();
    @MockBean
    BookingService bookingService;

    @Test
    void addTest() throws Exception {
        Item item1 = new Item(1L, "name1", "description1", true, new User(1L, "uname1", "user1@user.com"), null);
        User user2 = new User(2L, "name2", "user2@user.com");
        when(bookingService.add(any(BookingDto.class), anyLong())).thenAnswer(invocation -> {
            BookingDto argument = invocation.getArgument(0, BookingDto.class);
            return bookingMapper.toOutDto(new Booking(argument.getId(), argument.getStart(), argument.getEnd(),
                    item1, user2, argument.getStatus()), itemMapper.toDto(item1), userMapper.toDto(user2));
        });
        Booking booking = new Booking(1L, LocalDateTime.now(ZoneId.of("Europe/Moscow")), LocalDateTime.now(ZoneId.of("Europe/Moscow")),
                item1, user2, BookingStatus.WAITING);
        BookingDto bookingDto1 = bookingMapper.toInDto(booking);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBookerId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto1.getItemId()), Long.class));
    }

    @Test
    void approveTest() throws Exception {
        Item item1 = new Item(1L, "name1", "description1", true, new User(1L, "uname1", "user1@user.com"), null);
        User user2 = new User(2L, "name2", "user2@user.com");
        Booking booking = new Booking(1L, LocalDateTime.now(ZoneId.of("Europe/Moscow")), LocalDateTime.now(ZoneId.of("Europe/Moscow")),
                item1, user2, BookingStatus.WAITING);
        when(bookingService.approve(anyLong(), anyBoolean(), anyLong())).thenAnswer(invocation -> {
            Long bookingId = invocation.getArgument(0, Long.class);
            return bookingMapper.toOutDto(new Booking(bookingId, booking.getStart(), booking.getEnd(),
                    item1, user2, BookingStatus.APPROVED), itemMapper.toDto(item1), userMapper.toDto(user2));
        });
        BookingDto bookingDto1 = bookingMapper.toInDto(booking);
        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBookerId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED"), String.class));
    }

    @Test
    void getTest() throws Exception {
        Item item1 = new Item(1L, "name1", "description1", true, new User(1L, "uname1", "user1@user.com"), null);
        User user2 = new User(2L, "name2", "user2@user.com");
        Booking booking = new Booking(1L, LocalDateTime.now(ZoneId.of("Europe/Moscow")), LocalDateTime.now(ZoneId.of("Europe/Moscow")),
                item1, user2, BookingStatus.WAITING);
        when(bookingService.get(anyLong(), anyLong())).thenAnswer(invocation -> {
            Long bookingId = invocation.getArgument(0, Long.class);
            return bookingMapper.toOutDto(new Booking(bookingId, booking.getStart(), booking.getEnd(),
                    item1, user2, BookingStatus.APPROVED), itemMapper.toDto(item1), userMapper.toDto(user2));
        });
        BookingDto bookingDto1 = bookingMapper.toInDto(booking);
        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBookerId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED"), String.class));
    }

    @Test
    void getAllTest() throws Exception {
        Item item1 = new Item(1L, "name1", "description1", true, new User(1L, "uname1", "user1@user.com"), null);
        User user2 = new User(2L, "name2", "user2@user.com");
        Booking booking = new Booking(1L, LocalDateTime.now(ZoneId.of("Europe/Moscow")), LocalDateTime.now(ZoneId.of("Europe/Moscow")),
                item1, user2, BookingStatus.WAITING);
        when(bookingService.getAllByUserId(anyLong(), anyString(), anyInt(), anyInt())).thenAnswer(invocation -> List.of(bookingMapper.toOutDto(new Booking(booking.getId(), booking.getStart(), booking.getEnd(),
                item1, user2, BookingStatus.APPROVED), itemMapper.toDto(item1), userMapper.toDto(user2))));
        BookingDto bookingDto1 = bookingMapper.toInDto(booking);
        mockMvc.perform(get("/bookings", 1)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto1.getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].status", is("APPROVED"), String.class));
    }

    @Test
    void getAllForItemsTest() throws Exception {
        Item item1 = new Item(1L, "name1", "description1", true, new User(1L, "uname1", "user1@user.com"), null);
        User user2 = new User(2L, "name2", "user2@user.com");
        Booking booking = new Booking(1L, LocalDateTime.now(ZoneId.of("Europe/Moscow")), LocalDateTime.now(ZoneId.of("Europe/Moscow")),
                item1, user2, BookingStatus.WAITING);
        when(bookingService.getAllForItems(anyLong(), anyString(), anyInt(), anyInt())).thenAnswer(invocation -> List.of(bookingMapper.toOutDto(new Booking(booking.getId(), booking.getStart(), booking.getEnd(),
                item1, user2, BookingStatus.APPROVED), itemMapper.toDto(item1), userMapper.toDto(user2))));
        BookingDto bookingDto1 = bookingMapper.toInDto(booking);
        mockMvc.perform(get("/bookings/owner", 1)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto1.getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].status", is("APPROVED"), String.class));
    }
}
