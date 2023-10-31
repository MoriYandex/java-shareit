package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class DataJpaTests {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RequestRepository requestRepository;
    private final LocalDateTime pastDate1 = LocalDateTime.of(2022, 1, 1, 1, 1, 1);
    private final LocalDateTime pastDate2 = LocalDateTime.of(2023, 1, 1, 1, 1, 1);
    private final LocalDateTime futureDate1 = LocalDateTime.of(2024, 1, 1, 1, 1, 1);
    private final LocalDateTime futureDate2 = LocalDateTime.of(2025, 1, 1, 1, 1, 1);
    private final LocalDateTime futureDate3 = LocalDateTime.of(2026, 1, 1, 1, 1, 1);

    @Test
    void commentGetAllByOwnerTest() {
        User user1 = new User(1, "uname1", "user1@user.com");
        User user2 = new User(2, "uname2", "user2@user.com");
        User user3 = new User(3, "uname3", "user3@user.com");
        Request request1 = new Request(1, "request1", user3, pastDate1);
        Item item1 = new Item(1, "name1", "description1", true, user1, null);
        Item item2 = new Item(2, "name2", "description2", true, user2, request1);
        Item item3 = new Item(3, "name3", "description3", false, user2, request1);
        Booking booking1 = new Booking(1, pastDate1, pastDate2, item2, user2, BookingStatus.APPROVED);
        Booking booking2 = new Booking(2, pastDate1, futureDate2, item1, user2, BookingStatus.APPROVED);
        Booking booking3 = new Booking(3, futureDate1, futureDate3, item2, user3, BookingStatus.APPROVED);
        Booking booking4 = new Booking(4, futureDate2, futureDate3, item2, user3, BookingStatus.WAITING);
        Comment comment1 = new Comment(1, "text1", item1, user2, pastDate2);

        userRepository.saveAndFlush(user1);
        userRepository.saveAndFlush(user2);
        userRepository.saveAndFlush(user3);
        requestRepository.saveAndFlush(request1);
        itemRepository.saveAndFlush(item1);
        itemRepository.saveAndFlush(item2);
        itemRepository.saveAndFlush(item3);
        bookingRepository.saveAndFlush(booking1);
        bookingRepository.saveAndFlush(booking2);
        bookingRepository.saveAndFlush(booking3);
        bookingRepository.saveAndFlush(booking4);
        commentRepository.saveAndFlush(comment1);

        List<Comment> commentList = commentRepository.getAllByOwner(user1);
        Assertions.assertEquals(commentList.size(), 1);
        Assertions.assertEquals(commentList.get(0).getText(), "text1");
        List<Item> items = itemRepository.findAllByRequestor(user3);
        Assertions.assertEquals(items.size(), 2);
        Assertions.assertEquals(items.get(0).getId(), 2);
        Assertions.assertEquals(items.get(1).getId(), 3);
        List<Item> items2 = itemRepository.searchByText(true, "Ion", Pageable.unpaged()).toList();
        Assertions.assertEquals(items2.size(), 2);
        Assertions.assertEquals(items2.get(0).getId(), 1);
        Assertions.assertEquals(items2.get(1).getId(), 2);
        List<Item> items3 = itemRepository.searchByText(true, "aMe2", Pageable.unpaged()).toList();
        Assertions.assertEquals(items3.size(), 1);
        Assertions.assertEquals(items3.get(0).getId(), 2);
        List<Item> items4 = itemRepository.searchByText(false, "aMe3", Pageable.unpaged()).toList();
        Assertions.assertEquals(items4.size(), 1);
        Assertions.assertEquals(items4.get(0).getId(), 3);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerByStatus(user2, BookingStatus.APPROVED, Pageable.unpaged()).toList();
        Assertions.assertEquals(bookings.size(), 2);
        Assertions.assertEquals(bookings.get(0).getId(), 3);
        Assertions.assertEquals(bookings.get(1).getId(), 1);
        List<Booking> bookings2 = bookingRepository.findAllByItemOwnerFuture(user2, LocalDateTime.now(), Pageable.unpaged()).toList();
        Assertions.assertEquals(bookings2.size(), 2);
        Assertions.assertEquals(bookings2.get(0).getId(), 4);
        Assertions.assertEquals(bookings2.get(1).getId(), 3);
        List<Booking> bookings3 = bookingRepository.findAllByItemOwnerPast(user2, LocalDateTime.now(), Pageable.unpaged()).toList();
        Assertions.assertEquals(bookings3.size(), 1);
        Assertions.assertEquals(bookings3.get(0).getId(), 1);
        List<Booking> bookings4 = bookingRepository.findAllByItemOwnerCurrent(user1, LocalDateTime.now(), Pageable.unpaged()).toList();
        Assertions.assertEquals(bookings4.size(), 1);
        Assertions.assertEquals(bookings4.get(0).getId(), 2);
        List<Booking> bookings5 = bookingRepository.findAllByItemOwner(user2, Pageable.unpaged()).toList();
        Assertions.assertEquals(bookings5.size(), 3);
        Assertions.assertEquals(bookings5.get(0).getId(), 4);
        Assertions.assertEquals(bookings5.get(1).getId(), 3);
        Assertions.assertEquals(bookings5.get(2).getId(), 1);
    }
}
