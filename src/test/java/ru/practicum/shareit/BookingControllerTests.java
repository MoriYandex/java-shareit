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
        Item item1 = new Item(1, "name1", "description1", true, new User(1, "uname1", "user1@user.com"), null);
        User user2 = new User(2, "name2", "user2@user.com");
        when(bookingService.add(any(BookingDto.class), anyInt())).thenAnswer(invocation -> {
            BookingDto argument = invocation.getArgument(0, BookingDto.class);
            return bookingMapper.toOutDto(new Booking(argument.getId(), argument.getStart(), argument.getEnd(),
                    item1, user2, argument.getStatus()), itemMapper.toDto(item1), userMapper.toDto(user2));
        });
        Booking booking = new Booking(1, LocalDateTime.now(), LocalDateTime.now(),
                item1, user2, BookingStatus.WAITING);
        BookingDto bookingDto1 = bookingMapper.toInDto(booking);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Integer.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBookerId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto1.getItemId()), Integer.class));
    }

    @Test
    void approveTest() throws Exception {
        Item item1 = new Item(1, "name1", "description1", true, new User(1, "uname1", "user1@user.com"), null);
        User user2 = new User(2, "name2", "user2@user.com");
        Booking booking = new Booking(1, LocalDateTime.now(), LocalDateTime.now(),
                item1, user2, BookingStatus.WAITING);
        when(bookingService.approve(anyInt(), anyBoolean(), anyInt())).thenAnswer(invocation -> {
            Integer bookingId = invocation.getArgument(0, Integer.class);
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
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Integer.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBookerId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto1.getItemId()), Integer.class))
                .andExpect(jsonPath("$.status", is("APPROVED"), String.class));
    }

    @Test
    void getTest() throws Exception {
        Item item1 = new Item(1, "name1", "description1", true, new User(1, "uname1", "user1@user.com"), null);
        User user2 = new User(2, "name2", "user2@user.com");
        Booking booking = new Booking(1, LocalDateTime.now(), LocalDateTime.now(),
                item1, user2, BookingStatus.WAITING);
        when(bookingService.get(anyInt(), anyInt())).thenAnswer(invocation -> {
            Integer bookingId = invocation.getArgument(0, Integer.class);
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
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Integer.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBookerId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto1.getItemId()), Integer.class))
                .andExpect(jsonPath("$.status", is("APPROVED"), String.class));
    }

    @Test
    void getAllTest() throws Exception {
        Item item1 = new Item(1, "name1", "description1", true, new User(1, "uname1", "user1@user.com"), null);
        User user2 = new User(2, "name2", "user2@user.com");
        Booking booking = new Booking(1, LocalDateTime.now(), LocalDateTime.now(),
                item1, user2, BookingStatus.WAITING);
        when(bookingService.getAllByUserId(anyInt(), anyString(), anyInt(), anyInt())).thenAnswer(invocation -> List.of(bookingMapper.toOutDto(new Booking(booking.getId(), booking.getStart(), booking.getEnd(),
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
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Integer.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto1.getBookerId()), Integer.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto1.getItemId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is("APPROVED"), String.class));
    }

    @Test
    void getAllForItemsTest() throws Exception {
        Item item1 = new Item(1, "name1", "description1", true, new User(1, "uname1", "user1@user.com"), null);
        User user2 = new User(2, "name2", "user2@user.com");
        Booking booking = new Booking(1, LocalDateTime.now(), LocalDateTime.now(),
                item1, user2, BookingStatus.WAITING);
        when(bookingService.getAllForItems(anyInt(), anyString(), anyInt(), anyInt())).thenAnswer(invocation -> List.of(bookingMapper.toOutDto(new Booking(booking.getId(), booking.getStart(), booking.getEnd(),
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
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Integer.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto1.getBookerId()), Integer.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto1.getItemId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is("APPROVED"), String.class));
    }
}
