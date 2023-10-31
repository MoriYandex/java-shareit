package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoExtended;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.comment.service.CommentServiceImpl;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExtended;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoExtended;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShareItTests {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final RequestService requestService;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final RequestMapper requestMapper;
    private final EntityManager entityManager;

    @Test
    void contextLoads() {
    }

    @Test
    void userServiceUnitTest() {
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
        RequestRepository mockRequestRepository = Mockito.mock(RequestRepository.class);
        BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);
        UserMapper userMapper = new UserMapper();
        UserService userService = new UserServiceImpl(mockUserRepository, mockItemRepository, mockRequestRepository, mockBookingRepository, userMapper);
        User user1 = new User(1, "name1", "user1@user.com");
        User user2 = new User(2, "name2", "user2@user.com");
        Mockito.when(mockUserRepository.saveAndFlush(Mockito.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, User.class));
        Mockito.when(mockUserRepository.findAll()).thenReturn(List.of(user1, user2));
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenAnswer(invocation -> {
                    Integer userID = invocation.getArgument(0, Integer.class);
                    switch (userID) {
                        case 1:
                            return Optional.of(user1);
                        case 2:
                            return Optional.of(user2);
                        default:
                            return Optional.empty();
                    }
                });
        UserDto userDto1 = userService.add(userMapper.toDto(user1));
        Assertions.assertTrue(Objects.equals(userDto1.getId(), user1.getId())
                && userDto1.getName().equals(user1.getName())
                && userDto1.getEmail().equals(user1.getEmail()));
        UserDto userDto2 = userService.add(userMapper.toDto(user2));
        Assertions.assertTrue(Objects.equals(userDto2.getId(), user2.getId())
                && userDto2.getName().equals(user2.getName())
                && userDto2.getEmail().equals(user2.getEmail()));
        user1.setEmail("user1new@user.com");
        userDto1 = userService.update(userMapper.toDto(user1), 1);
        Assertions.assertTrue(Objects.equals(userDto1.getId(), user1.getId())
                && userDto1.getName().equals(user1.getName())
                && userDto1.getEmail().equals(user1.getEmail()));
        Assertions.assertThrows(NotFoundException.class, () -> userService.update(userMapper.toDto(user1), 3));
        userDto2 = userService.get(2);
        Assertions.assertTrue(Objects.equals(userDto2.getId(), user2.getId())
                && userDto2.getName().equals(user2.getName())
                && userDto2.getEmail().equals(user2.getEmail()));
        Assertions.assertThrows(NotFoundException.class, () -> userService.get(3));
        List<UserDto> userDtoList = userService.getAll();
        userDto1 = userDtoList.get(0);
        userDto2 = userDtoList.get(1);
        Assertions.assertTrue(Objects.equals(userDto1.getId(), user1.getId())
                && userDto1.getName().equals(user1.getName())
                && userDto1.getEmail().equals(user1.getEmail()));
        Assertions.assertTrue(Objects.equals(userDto2.getId(), user2.getId())
                && userDto2.getName().equals(user2.getName())
                && userDto2.getEmail().equals(user2.getEmail()));
        Mockito.when(mockBookingRepository.findAllByBookerOrderByStartDesc(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenReturn(Page.empty());
        Mockito.when(mockRequestRepository.findAllByRequestorOrderByCreatedDesc(Mockito.any(User.class)))
                .thenReturn(new ArrayList<>());
        Mockito.when(mockItemRepository.findAllByOwner(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenReturn(Page.empty());
        userService.delete(1);
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .deleteById(1);
        Mockito.when(mockBookingRepository.findAllByBookerOrderByStartDesc(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Booking(1, LocalDateTime.now(), LocalDateTime.now(), null, null, APPROVED))));
        Assertions.assertThrows(ValidationException.class, () -> userService.delete(1));
        Mockito.when(mockBookingRepository.findAllByBookerOrderByStartDesc(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenReturn(Page.empty());
        Mockito.when(mockRequestRepository.findAllByRequestorOrderByCreatedDesc(Mockito.any(User.class)))
                .thenReturn(List.of(new Request(1, "", null, LocalDateTime.now())));
        Assertions.assertThrows(ValidationException.class, () -> userService.delete(1));
        Mockito.when(mockRequestRepository.findAllByRequestorOrderByCreatedDesc(Mockito.any(User.class)))
                .thenReturn(new ArrayList<>());
        Mockito.when(mockItemRepository.findAllByOwner(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Item(1, "", "", true, null, null))));
        Assertions.assertThrows(ValidationException.class, () -> userService.delete(1));
        Mockito.when(mockItemRepository.findAllByOwner(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenReturn(Page.empty());
        userService.delete(1);
        Mockito.verify(mockUserRepository, Mockito.times(2))
                .deleteById(1);
    }

    @Test
    void itemServiceUnitTest() {
        ItemMapper itemMapper = new ItemMapper();
        BookingMapper bookingMapper = new BookingMapper();
        CommentMapper commentMapper = new CommentMapper();
        ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        RequestRepository mockRequestRepository = Mockito.mock(RequestRepository.class);
        BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);
        CommentRepository mockCommentRepository = Mockito.mock(CommentRepository.class);
        ItemService itemService = new ItemServiceImpl(itemMapper,
                bookingMapper,
                commentMapper,
                mockItemRepository,
                mockUserRepository,
                mockRequestRepository,
                mockBookingRepository,
                mockCommentRepository);
        Mockito.when(mockItemRepository.saveAndFlush(Mockito.any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Item.class));
        User user1 = new User(1, "name1", "user1@user.com");
        Request request1 = new Request(1, "", null, LocalDateTime.now());
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, Integer.class), 1)
                        ? Optional.of(user1)
                        : Optional.empty());
        Mockito.when(mockRequestRepository.findById(Mockito.anyInt()))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, Integer.class), 1)
                        ? Optional.of(request1)
                        : Optional.empty());
        Item item1 = new Item(1, "", "", true, user1, request1);
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, Integer.class), 1)
                        ? Optional.of(item1)
                        : Optional.empty());
        ItemDto itemDto1 = itemService.add(itemMapper.toDto(item1), user1.getId());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.add(itemMapper.toDto(item1), 2));
        Assertions.assertTrue(Objects.equals(itemDto1.getId(), item1.getId())
                && Objects.equals(itemDto1.getName(), item1.getName())
                && Objects.equals(itemDto1.getDescription(), item1.getDescription())
                && Objects.equals(itemDto1.getAvailable(), item1.getAvailable())
                && Objects.equals(itemDto1.getRequestId(), item1.getRequest().getId())
                && Objects.equals(itemDto1.getOwnerId(), item1.getOwner().getId()));
        item1.setDescription("description");
        itemDto1 = itemService.update(itemMapper.toDto(item1), 1, 1);
        Assertions.assertTrue(Objects.equals(itemDto1.getId(), item1.getId())
                && Objects.equals(itemDto1.getName(), item1.getName())
                && Objects.equals(itemDto1.getDescription(), item1.getDescription())
                && Objects.equals(itemDto1.getAvailable(), item1.getAvailable())
                && Objects.equals(itemDto1.getRequestId(), item1.getRequest().getId())
                && Objects.equals(itemDto1.getOwnerId(), item1.getOwner().getId()));
        Assertions.assertThrows(NotFoundException.class, () -> itemService.update(itemMapper.toDto(item1), 1, 2));
        Assertions.assertThrows(ForbiddenException.class, () -> itemService.update(itemMapper.toDto(item1), 2, 1));
        Mockito.when(mockCommentRepository.getAllByItemOrderByCreatedDesc(Mockito.any(Item.class)))
                .thenReturn(new ArrayList<>());
        Mockito.when(mockBookingRepository.findFirstByItemAndStatusAndStartIsAfterOrderByStart(Mockito.any(Item.class), Mockito.any(BookingStatus.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(null);
        Mockito.when(mockBookingRepository.findFirstByItemAndStatusAndStartIsBeforeOrderByEndDesc(Mockito.any(Item.class), Mockito.any(BookingStatus.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(null);
        ItemDtoExtended itemDto2 = itemService.get(1, 1);
        Assertions.assertTrue(Objects.equals(itemDto2.getId(), item1.getId())
                && Objects.equals(itemDto2.getName(), item1.getName())
                && Objects.equals(itemDto2.getDescription(), item1.getDescription())
                && Objects.equals(itemDto2.getAvailable(), item1.getAvailable())
                && Objects.equals(itemDto2.getRequestId(), item1.getRequest().getId())
                && Objects.equals(itemDto2.getOwnerId(), item1.getOwner().getId()));
        Assertions.assertThrows(NotFoundException.class, () -> itemService.get(2, 1));
        Mockito.when(mockItemRepository.findAllByOwner(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item1)));
        Mockito.when(mockBookingRepository.findAllByItemInOrderByStartDesc(Mockito.any(ArrayList.class)))
                .thenReturn(new ArrayList<>());
        Mockito.when(mockCommentRepository.getAllByOwner(Mockito.any(User.class)))
                .thenReturn(new ArrayList<>());
        List<ItemDtoExtended> list1 = itemService.getAllByUserExtended(1, 1, 1);
        itemDto2 = list1.get(0);
        Assertions.assertTrue(Objects.equals(itemDto2.getId(), item1.getId())
                && Objects.equals(itemDto2.getName(), item1.getName())
                && Objects.equals(itemDto2.getDescription(), item1.getDescription())
                && Objects.equals(itemDto2.getAvailable(), item1.getAvailable())
                && Objects.equals(itemDto2.getRequestId(), item1.getRequest().getId())
                && Objects.equals(itemDto2.getOwnerId(), item1.getOwner().getId()));
        Assertions.assertThrows(NotFoundException.class, () -> itemService.getAllByUserExtended(2, 1, 1));
        Assertions.assertThrows(ValidationException.class, () -> itemService.getAllByUserExtended(1, -1, 1));
        Assertions.assertThrows(ValidationException.class, () -> itemService.getAllByUserExtended(1, 1, 0));
        Mockito.when(mockItemRepository.searchByText(Mockito.anyBoolean(), Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenAnswer(invocation -> item1.getDescription().toLowerCase().contains(invocation.getArgument(1, String.class).toLowerCase())
                        ? new PageImpl<>(List.of(item1))
                        : Page.empty());
        List<ItemDto> list2 = itemService.getAvailableByText("desc", 1, 1);
        itemDto1 = list2.get(0);
        Assertions.assertTrue(Objects.equals(itemDto1.getId(), item1.getId())
                && Objects.equals(itemDto1.getName(), item1.getName())
                && Objects.equals(itemDto1.getDescription(), item1.getDescription())
                && Objects.equals(itemDto1.getAvailable(), item1.getAvailable())
                && Objects.equals(itemDto1.getRequestId(), item1.getRequest().getId())
                && Objects.equals(itemDto1.getOwnerId(), item1.getOwner().getId()));
        list2 = itemService.getAvailableByText("desci", 1, 1);
        Assertions.assertTrue(list2.isEmpty());
        Assertions.assertThrows(ValidationException.class, () -> itemService.getAvailableByText("desc", -1, 1));
        Assertions.assertThrows(ValidationException.class, () -> itemService.getAvailableByText("desc", 1, 0));
    }

    @Test
    void requestServiceUnitTest() {
        RequestMapper requestMapper = new RequestMapper();
        ItemMapper itemMapper = new ItemMapper();
        RequestRepository mockRequestRepository = Mockito.mock(RequestRepository.class);
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
        RequestService requestService = new RequestServiceImpl(mockRequestRepository, mockUserRepository, mockItemRepository, requestMapper, itemMapper);
        User user1 = new User(1, "name1", "user1@user.com");
        User user2 = new User(2, "name2", "user2@user.com");
        Request request1 = new Request(1, "", user1, null);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenAnswer(invocation -> {
                    switch (invocation.getArgument(0, Integer.class)) {
                        case 1:
                            return Optional.of(user1);
                        case 2:
                            return Optional.of(user2);
                        default:
                            return Optional.empty();
                    }
                });
        Mockito.when(mockRequestRepository.saveAndFlush(Mockito.any(Request.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Request.class));
        RequestDto requestDto1 = requestService.add(requestMapper.toInDto(request1), 1);
        Assertions.assertTrue(Objects.equals(requestDto1.getId(), request1.getId())
                && Objects.equals(requestDto1.getDescription(), request1.getDescription())
                && Objects.equals(requestDto1.getRequestorId(), request1.getRequestor().getId()));
        Assertions.assertThrows(NotFoundException.class, () -> requestService.add(requestMapper.toInDto(request1), 3));
        Mockito.when(mockRequestRepository.findById(Mockito.anyInt()))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, Integer.class), 1)
                        ? Optional.of(request1)
                        : Optional.empty());
        Mockito.when(mockItemRepository.findAllByRequest(Mockito.any(Request.class)))
                .thenReturn(new ArrayList<>());
        RequestDtoExtended requestDtoExtended1 = requestService.getById(1, 1);
        Assertions.assertTrue(Objects.equals(requestDtoExtended1.getId(), request1.getId())
                && Objects.equals(requestDtoExtended1.getDescription(), request1.getDescription())
                && Objects.equals(requestDtoExtended1.getRequestorId(), request1.getRequestor().getId()));
        Assertions.assertThrows(NotFoundException.class, () -> requestService.getById(1, 3));
        Assertions.assertThrows(NotFoundException.class, () -> requestService.getById(2, 1));
        Mockito.when(mockRequestRepository.findAllByRequestorOrderByCreatedDesc(Mockito.any(User.class)))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, User.class).getId(), request1.getRequestor().getId())
                        ? List.of(request1)
                        : new ArrayList<>());
        Mockito.when(mockItemRepository.findAllByRequestor(Mockito.any(User.class)))
                .thenReturn(new ArrayList<>());
        List<RequestDtoExtended> requestDtoExtendedList1 = requestService.getAllByUserId(1);
        requestDtoExtended1 = requestDtoExtendedList1.get(0);
        Assertions.assertTrue(Objects.equals(requestDtoExtended1.getId(), request1.getId())
                && Objects.equals(requestDtoExtended1.getDescription(), request1.getDescription())
                && Objects.equals(requestDtoExtended1.getRequestorId(), request1.getRequestor().getId()));
        Assertions.assertThrows(NotFoundException.class, () -> requestService.getAllByUserId(3));
        Mockito.when(mockRequestRepository.findAllByRequestorIsNot(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, User.class).getId(), 1)
                        ? Page.empty()
                        : new PageImpl<>(List.of(request1)));
        Mockito.when(mockItemRepository.findAllByRequestIn(Mockito.any(ArrayList.class)))
                .thenReturn(new ArrayList<>());
        requestDtoExtendedList1 = requestService.getAll(2, 1, 1);
        requestDtoExtended1 = requestDtoExtendedList1.get(0);
        Assertions.assertTrue(Objects.equals(requestDtoExtended1.getId(), request1.getId())
                && Objects.equals(requestDtoExtended1.getDescription(), request1.getDescription())
                && Objects.equals(requestDtoExtended1.getRequestorId(), request1.getRequestor().getId()));
        Assertions.assertThrows(ValidationException.class, () -> requestService.getAll(2, -1, 1));
        Assertions.assertThrows(ValidationException.class, () -> requestService.getAll(2, 0, 0));
        Assertions.assertThrows(NotFoundException.class, () -> requestService.getAll(3, 1, 1));
        requestDtoExtendedList1 = requestService.getAll(1, 1, 1);
        Assertions.assertTrue(requestDtoExtendedList1.isEmpty());
    }

    @Test
    void commentServiceUnitTest() {
        CommentMapper commentMapper = new CommentMapper();
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
        BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);
        CommentRepository mockCommentRepository = Mockito.mock(CommentRepository.class);
        CommentService commentService = new CommentServiceImpl(mockCommentRepository, mockUserRepository, mockItemRepository, mockBookingRepository, commentMapper);
        User user1 = new User(1, "name1", "user1@user.com");
        Item item1 = new Item(1, "name", "description", true, user1, null);
        Booking booking1 = new Booking(1, null, null, item1, user1, APPROVED);
        Comment comment1 = new Comment(1, "text", item1, user1, null);
        Mockito.when(mockCommentRepository.saveAndFlush(Mockito.any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Comment.class));
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, Integer.class), user1.getId())
                        ? Optional.of(user1)
                        : Optional.empty());
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, Integer.class), item1.getId())
                        ? Optional.of(item1)
                        : Optional.empty());
        Mockito.when(mockBookingRepository.findAllByItemAndBookerAndStatusAndEndIsBefore(Mockito.any(Item.class),
                        Mockito.any(User.class),
                        Mockito.any(BookingStatus.class),
                        Mockito.any(LocalDateTime.class)))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, Item.class).getId(), item1.getId())
                        && Objects.equals(invocation.getArgument(1, User.class).getId(), user1.getId())
                        ? List.of(booking1)
                        : new ArrayList<>());
        CommentDto commentDto1 = commentService.add(commentMapper.toDto(comment1), 1, 1);
        Assertions.assertTrue(Objects.equals(commentDto1.getId(), comment1.getId())
                && Objects.equals(commentDto1.getText(), comment1.getText())
                && Objects.equals(commentDto1.getAuthorId(), comment1.getAuthor().getId())
                && Objects.equals(commentDto1.getItemId(), comment1.getItem().getId()));
        Assertions.assertThrows(NotFoundException.class, () -> commentService.add(commentMapper.toDto(comment1), 2, 1));
        Assertions.assertThrows(NotFoundException.class, () -> commentService.add(commentMapper.toDto(comment1), 1, 2));
    }

    @Test
    void bookingServiceUnitTest() {
        BookingMapper bookingMapper = new BookingMapper();
        ItemMapper itemMapper = new ItemMapper();
        UserMapper userMapper = new UserMapper();
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
        BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);
        BookingService bookingService = new BookingServiceImpl(mockBookingRepository, mockUserRepository, mockItemRepository, bookingMapper, itemMapper, userMapper);
        Mockito.when(mockBookingRepository.saveAndFlush(Mockito.any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Booking.class));
        User user1 = new User(1, "name1", "user1@user.com");
        User user2 = new User(2, "name2", "user2@user.com");
        Item item1 = new Item(1, "name", "description", true, user1, null);
        Booking booking1 = new Booking(1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), item1, user2, BookingStatus.WAITING);
        Booking booking2 = new Booking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item1, user2, BookingStatus.WAITING);
        Booking booking3 = new Booking(1, null, null, item1, user2, BookingStatus.WAITING);
        Booking booking4 = new Booking(1, null, null, item1, user1, BookingStatus.WAITING);
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, Integer.class), item1.getId())
                        ? Optional.of(item1)
                        : Optional.empty());
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenAnswer(invocation -> {
                    switch (invocation.getArgument(0, Integer.class)) {
                        case 1:
                            return Optional.of(user1);
                        case 2:
                            return Optional.of(user2);
                        default:
                            return Optional.empty();
                    }
                });
        Mockito.when(mockBookingRepository.findById(Mockito.anyInt()))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, Integer.class), booking1.getId())
                        ? Optional.of(booking1)
                        : Optional.empty());
        Mockito.when(mockBookingRepository.findAllByBookerOrderByStartDesc(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, User.class).getId(), user2.getId())
                        ? new PageImpl<>(List.of(booking1))
                        : Page.empty());
        Mockito.when(mockBookingRepository.findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Mockito.any(User.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, User.class).getId(), user2.getId())
                        ? new PageImpl<>(List.of(booking1))
                        : Page.empty());
        Mockito.when(mockBookingRepository.findAllByBookerAndEndIsBeforeOrderByStartDesc(Mockito.any(User.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, User.class).getId(), user2.getId())
                        ? new PageImpl<>(List.of(booking1))
                        : Page.empty());
        Mockito.when(mockBookingRepository.findAllByBookerAndStartIsAfterOrderByStartDesc(Mockito.any(User.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, User.class).getId(), user2.getId())
                        ? new PageImpl<>(List.of(booking1))
                        : Page.empty());
        Mockito.when(mockBookingRepository.findAllByBookerAndStatusOrderByStartDesc(Mockito.any(User.class),
                        Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class)))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, User.class).getId(), user2.getId())
                        ? new PageImpl<>(List.of(booking1))
                        : Page.empty());
        Mockito.when(mockBookingRepository.findAllByItemOwner(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, User.class).getId(), user1.getId())
                        ? new PageImpl<>(List.of(booking1))
                        : Page.empty());
        Mockito.when(mockBookingRepository.findAllByItemOwnerCurrent(Mockito.any(User.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, User.class).getId(), user1.getId())
                        ? new PageImpl<>(List.of(booking1))
                        : Page.empty());
        Mockito.when(mockBookingRepository.findAllByItemOwnerPast(Mockito.any(User.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, User.class).getId(), user1.getId())
                        ? new PageImpl<>(List.of(booking1))
                        : Page.empty());
        Mockito.when(mockBookingRepository.findAllByItemOwnerFuture(Mockito.any(User.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, User.class).getId(), user1.getId())
                        ? new PageImpl<>(List.of(booking1))
                        : Page.empty());
        Mockito.when(mockBookingRepository.findAllByItemOwnerByStatus(Mockito.any(User.class),
                        Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class)))
                .thenAnswer(invocation -> Objects.equals(invocation.getArgument(0, User.class).getId(), user1.getId())
                        ? new PageImpl<>(List.of(booking1))
                        : Page.empty());
        BookingDtoExtended bookingDtoExtended1 = bookingService.add(bookingMapper.toInDto(booking1), 2);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.add(bookingMapper.toInDto(booking1), 1));
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.add(bookingMapper.toInDto(booking2), 1));
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.add(bookingMapper.toInDto(booking3), 1));
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.add(bookingMapper.toInDto(booking4), 1));
        bookingDtoExtended1 = bookingService.get(1, 2);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        bookingDtoExtended1 = bookingService.get(1, 1);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.get(2, 1));
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.get(1, 3));
        List<BookingDtoExtended> bookingDtoExtendedList1 = bookingService.getAllByUserId(2, "ALL", 1, 1);
        bookingDtoExtended1 = bookingDtoExtendedList1.get(0);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        bookingDtoExtendedList1 = bookingService.getAllByUserId(2, "FUTURE", 1, 1);
        bookingDtoExtended1 = bookingDtoExtendedList1.get(0);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        bookingDtoExtendedList1 = bookingService.getAllByUserId(2, "CURRENT", 1, 1);
        bookingDtoExtended1 = bookingDtoExtendedList1.get(0);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        bookingDtoExtendedList1 = bookingService.getAllByUserId(2, "PAST", 1, 1);
        bookingDtoExtended1 = bookingDtoExtendedList1.get(0);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        bookingDtoExtendedList1 = bookingService.getAllByUserId(2, "WAITING", 1, 1);
        bookingDtoExtended1 = bookingDtoExtendedList1.get(0);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        bookingDtoExtendedList1 = bookingService.getAllByUserId(2, "REJECTED", 1, 1);
        bookingDtoExtended1 = bookingDtoExtendedList1.get(0);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        Assertions.assertThrows(UnsupportedException.class, () -> bookingService.getAllByUserId(2, "UNSUPPORTED_CLASS", 1, 1));
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.approve(1, true, 2));
        bookingDtoExtended1 = bookingService.approve(1, true, 1);
        booking1.setStatus(APPROVED);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        Assertions.assertThrows(ValidationException.class, () -> bookingService.approve(1, false, 1));
        bookingDtoExtendedList1 = bookingService.getAllForItems(1, "ALL", 1, 1);
        bookingDtoExtended1 = bookingDtoExtendedList1.get(0);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        bookingDtoExtendedList1 = bookingService.getAllForItems(1, "FUTURE", 1, 1);
        bookingDtoExtended1 = bookingDtoExtendedList1.get(0);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        bookingDtoExtendedList1 = bookingService.getAllForItems(1, "CURRENT", 1, 1);
        bookingDtoExtended1 = bookingDtoExtendedList1.get(0);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        bookingDtoExtendedList1 = bookingService.getAllForItems(1, "PAST", 1, 1);
        bookingDtoExtended1 = bookingDtoExtendedList1.get(0);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        bookingDtoExtendedList1 = bookingService.getAllForItems(1, "WAITING", 1, 1);
        bookingDtoExtended1 = bookingDtoExtendedList1.get(0);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        bookingDtoExtendedList1 = bookingService.getAllForItems(1, "REJECTED", 1, 1);
        bookingDtoExtended1 = bookingDtoExtendedList1.get(0);
        Assertions.assertTrue(Objects.equals(bookingDtoExtended1.getId(), booking1.getId())
                && Objects.equals(bookingDtoExtended1.getStatus(), booking1.getStatus())
                && Objects.equals(bookingDtoExtended1.getItem().getId(), booking1.getItem().getId())
                && Objects.equals(bookingDtoExtended1.getBooker().getId(), booking1.getBooker().getId()));
        Assertions.assertThrows(UnsupportedException.class, () -> bookingService.getAllForItems(1, "UNSUPPORTED_CLASS", 1, 1));
    }

    @Test
    void userServiceAddTest() {
        User user1 = new User(null, "name1", "user1@user.com");
        User user2 = new User(null, "name2", "user1@user.com");
        userService.add(userMapper.toDto(user1));
        TypedQuery<User> query = entityManager.createQuery(" from User u order by u.id", User.class);
        List<User> users = query.getResultList();
        Assertions.assertEquals(users.size(), 1);
        User newUser1 = users.get(0);
        Assertions.assertTrue(Objects.equals(user1.getName(), newUser1.getName())
                && Objects.equals(user1.getEmail(), newUser1.getEmail()));
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> userService.add(userMapper.toDto(user2)));
    }

    @Test
    void userServiceUpdateTest() {
        User user1 = new User(null, "name1", "user1@user.com");
        userService.add(userMapper.toDto(user1));
        TypedQuery<User> query = entityManager.createQuery(" from User u order by u.id", User.class);
        List<User> users = query.getResultList();
        Assertions.assertEquals(users.size(), 1);
        User newUser1 = users.get(0);
        user1.setEmail("user1new@user.com");
        userService.update(userMapper.toDto(user1), newUser1.getId());
        TypedQuery<User> query1 = entityManager.createQuery(" from User u order by u.id", User.class);
        List<User> users1 = query1.getResultList();
        User gotUser = users1.get(0);
        Assertions.assertTrue(Objects.equals(user1.getName(), gotUser.getName())
                && Objects.equals(user1.getEmail(), gotUser.getEmail()));
        User user2 = new User(null, "name2", "user2@user.com");
        userService.add(userMapper.toDto(user2));
        user1.setEmail("user2@user.com");
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> userService.update(userMapper.toDto(user1), gotUser.getId()));
    }

    @Test
    void userServiceDeleteTest() {
        User user1 = new User(null, "name1", "user1@user.com");
        userService.add(userMapper.toDto(user1));
        TypedQuery<User> query = entityManager.createQuery(" from User u order by u.id", User.class);
        List<User> users = query.getResultList();
        Assertions.assertEquals(users.size(), 1);
        userService.delete(users.get(0).getId());
        users = query.getResultList();
        Assertions.assertEquals(users.size(), 0);
    }

    @Test
    void userServiceGetTest() {
        User user1 = new User(null, "name1", "user1@user.com");
        userService.add(userMapper.toDto(user1));
        TypedQuery<User> query = entityManager.createQuery(" from User u order by u.id", User.class);
        List<User> users = query.getResultList();
        Assertions.assertEquals(users.size(), 1);
        UserDto userDto1 = userService.get(users.get(0).getId());
        Assertions.assertTrue(Objects.equals(users.get(0).getId(), userDto1.getId())
                && Objects.equals(users.get(0).getName(), userDto1.getName())
                && Objects.equals(users.get(0).getEmail(), userDto1.getEmail()));
    }

    @Test
    void userServiceGetAllTest() {
        User user1 = new User(null, "name1", "user1@user.com");
        User user2 = new User(null, "name2", "user2@user.com");
        userService.add(userMapper.toDto(user1));
        userService.add(userMapper.toDto(user2));
        TypedQuery<User> query = entityManager.createQuery(" from User u order by u.id", User.class);
        List<User> users = query.getResultList();
        Assertions.assertEquals(users.size(), 2);
        List<UserDto> userDtoList = userService.getAll();
        Assertions.assertEquals(userDtoList.size(), 2);
        Assertions.assertTrue(Objects.equals(users.get(0).getId(), userDtoList.get(0).getId())
                && Objects.equals(users.get(0).getName(), userDtoList.get(0).getName())
                && Objects.equals(users.get(0).getEmail(), userDtoList.get(0).getEmail()));
        Assertions.assertTrue(Objects.equals(users.get(1).getId(), userDtoList.get(1).getId())
                && Objects.equals(users.get(1).getName(), userDtoList.get(1).getName())
                && Objects.equals(users.get(1).getEmail(), userDtoList.get(1).getEmail()));
    }

    @Test
    void requestServiceAddTest() {
        User user1 = new User(null, "name1", "user1@user.com");
        userService.add(userMapper.toDto(user1));
        TypedQuery<User> userQuery = entityManager.createQuery(" from User u order by u.id", User.class);
        List<User> users = userQuery.getResultList();
        User userToRequest = users.get(0);
        Request request = new Request(null, "description", userToRequest, null);
        requestService.add(requestMapper.toInDto(request), userToRequest.getId());
        TypedQuery<Request> requestQuery = entityManager.createQuery(" from Request r order by r.id", Request.class);
        List<Request> requests = requestQuery.getResultList();
        Assertions.assertEquals(requests.size(), 1);
        Request newRequest = requests.get(0);
        Assertions.assertTrue(Objects.equals(newRequest.getDescription(), request.getDescription())
                && Objects.equals(newRequest.getRequestor().getId(), request.getRequestor().getId()));
    }

    @Test
    void requestServiceGetByIdTest() {
        User user1 = new User(null, "name1", "user1@user.com");
        userService.add(userMapper.toDto(user1));
        TypedQuery<User> userQuery = entityManager.createQuery(" from User u order by u.id", User.class);
        List<User> users = userQuery.getResultList();
        User userToRequest = users.get(0);
        Request request = new Request(null, "description", userToRequest, null);
        requestService.add(requestMapper.toInDto(request), userToRequest.getId());
        TypedQuery<Request> requestQuery = entityManager.createQuery(" from Request r order by r.id", Request.class);
        List<Request> requests = requestQuery.getResultList();
        Assertions.assertEquals(requests.size(), 1);
        Request newRequest = requests.get(0);
        Assertions.assertThrows(NotFoundException.class, () -> requestService.getById(1000, userToRequest.getId()));
        Assertions.assertThrows(NotFoundException.class, () -> requestService.getById(newRequest.getId(), 1000));
        RequestDtoExtended gotRequest = requestService.getById(newRequest.getId(), userToRequest.getId());
        Assertions.assertTrue(Objects.equals(newRequest.getDescription(), gotRequest.getDescription())
                && Objects.equals(gotRequest.getRequestorId(), newRequest.getRequestor().getId()));
    }

    @Test
    void requestServiceGetAllTest() {
        User user1 = new User(null, "name1", "user1@user.com");
        User user2 = new User(null, "name2", "user2@user.com");
        userService.add(userMapper.toDto(user1));
        userService.add(userMapper.toDto(user2));
        TypedQuery<User> query = entityManager.createQuery(" from User u order by u.id", User.class);
        List<User> users = query.getResultList();
        Request request1 = new Request(null, "description1", users.get(0), null);
        Request request2 = new Request(null, "description1", users.get(1), null);
        requestService.add(requestMapper.toInDto(request1), users.get(0).getId());
        requestService.add(requestMapper.toInDto(request2), users.get(1).getId());
        TypedQuery<Request> requestQuery = entityManager.createQuery(" from Request r order by r.id", Request.class);
        List<Request> requests = requestQuery.getResultList();
        Item item1 = new Item(null, "name1", "description1", true, users.get(0), requests.get(1));
        Item item2 = new Item(null, "name2", "description2", true, users.get(1), requests.get(0));
        itemService.add(itemMapper.toDto(item1), users.get(0).getId());
        itemService.add(itemMapper.toDto(item2), users.get(1).getId());
        Assertions.assertThrows(NotFoundException.class, () -> requestService.getAll(1000, 0, 10));
        Assertions.assertThrows(ValidationException.class, () -> requestService.getAll(users.get(0).getId(), -1, 10));
        Assertions.assertThrows(ValidationException.class, () -> requestService.getAll(users.get(0).getId(), 0, 0));
        List<RequestDtoExtended> gotRequests1 = requestService.getAll(users.get(0).getId(), 0, 10);
        Assertions.assertEquals(gotRequests1.size(), 1);
        Assertions.assertEquals(gotRequests1.get(0).getItems().get(0).getName(), "name1");
        List<RequestDtoExtended> gotRequests2 = requestService.getAll(users.get(1).getId(), 0, 10);
        Assertions.assertEquals(gotRequests2.size(), 1);
        Assertions.assertEquals(gotRequests2.get(0).getItems().get(0).getName(), "name2");
    }

    @Test
    void requestServiceGetAllByUserIdTest() {
        User user1 = new User(null, "name1", "user1@user.com");
        User user2 = new User(null, "name2", "user2@user.com");
        userService.add(userMapper.toDto(user1));
        userService.add(userMapper.toDto(user2));
        TypedQuery<User> query = entityManager.createQuery(" from User u order by u.id", User.class);
        List<User> users = query.getResultList();
        Request request1 = new Request(null, "description1", users.get(0), null);
        Request request2 = new Request(null, "description1", users.get(1), null);
        requestService.add(requestMapper.toInDto(request1), users.get(0).getId());
        requestService.add(requestMapper.toInDto(request2), users.get(1).getId());
        TypedQuery<Request> requestQuery = entityManager.createQuery(" from Request r order by r.id", Request.class);
        List<Request> requests = requestQuery.getResultList();
        Item item1 = new Item(null, "name1", "description1", true, users.get(0), requests.get(1));
        Item item2 = new Item(null, "name2", "description2", true, users.get(1), requests.get(0));
        itemService.add(itemMapper.toDto(item1), users.get(0).getId());
        itemService.add(itemMapper.toDto(item2), users.get(1).getId());
        Assertions.assertThrows(NotFoundException.class, () -> requestService.getAllByUserId(1000));
        List<RequestDtoExtended> gotRequests1 = requestService.getAllByUserId(users.get(0).getId());
        Assertions.assertEquals(gotRequests1.size(), 1);
        Assertions.assertEquals(gotRequests1.get(0).getItems().get(0).getName(), "name2");
    }

    @Test
    void itemServiceAddTest() {
        User user1 = new User(null, "name1", "user1@user.com");
        userService.add(userMapper.toDto(user1));
        TypedQuery<User> query = entityManager.createQuery(" from User u order by u.id", User.class);
        List<User> users = query.getResultList();
        Request request1 = new Request(null, "description1", users.get(0), null);
        requestService.add(requestMapper.toInDto(request1), users.get(0).getId());
        TypedQuery<Request> requestQuery = entityManager.createQuery(" from Request r order by r.id", Request.class);
        List<Request> requests = requestQuery.getResultList();
        Item item1 = new Item(null, "name1", "description1", true, users.get(0), null);
        Item item2 = new Item(null, "name2", "description2", true, users.get(0), requests.get(0));
        itemService.add(itemMapper.toDto(item1), users.get(0).getId());
        itemService.add(itemMapper.toDto(item2), users.get(0).getId());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.add(itemMapper.toDto(item2), 1000));
        TypedQuery<Item> itemTypedQuery = entityManager.createQuery(" from Item i order by i.id", Item.class);
        List<Item> items = itemTypedQuery.getResultList();
        Assertions.assertEquals(items.get(0).getDescription(), "description1");
        Assertions.assertEquals(items.get(1).getDescription(), "description2");
        Assertions.assertNull(items.get(0).getRequest());
        Assertions.assertEquals(items.get(1).getRequest().getId(), requests.get(0).getId());
        Assertions.assertEquals(items.get(0).getOwner().getName(), "name1");
        Assertions.assertEquals(items.get(1).getOwner().getName(), "name1");
    }

    @Test
    void itemServiceGetAllByUserExtendedTest() {
        User user1 = new User(null, "name1", "user1@user.com");
        User user2 = new User(null, "name2", "user2@user.com");
        userService.add(userMapper.toDto(user1));
        userService.add(userMapper.toDto(user2));
        TypedQuery<User> query = entityManager.createQuery(" from User u order by u.id", User.class);
        List<User> users = query.getResultList();
        Request request1 = new Request(null, "description1", users.get(0), null);
        requestService.add(requestMapper.toInDto(request1), users.get(0).getId());
        TypedQuery<Request> requestQuery = entityManager.createQuery(" from Request r order by r.id", Request.class);
        List<Request> requests = requestQuery.getResultList();
        Item item1 = new Item(null, "name1", "description1", true, users.get(0), null);
        Item item2 = new Item(null, "name2", "description2", true, users.get(0), requests.get(0));
        itemService.add(itemMapper.toDto(item1), users.get(0).getId());
        itemService.add(itemMapper.toDto(item2), users.get(0).getId());
        TypedQuery<Item> itemTypedQuery = entityManager.createQuery(" from Item i order by i.id", Item.class);
        List<Item> items = itemTypedQuery.getResultList();
        Booking booking1 = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), items.get(0), users.get(1), APPROVED);
        Booking booking2 = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), items.get(0), users.get(1), APPROVED);
        bookingService.add(bookingMapper.toInDto(booking1), users.get(1).getId());
        bookingService.add(bookingMapper.toInDto(booking2), users.get(1).getId());
        TypedQuery<Booking> bookingTypedQuery = entityManager.createQuery(" from Booking b order by b.id", Booking.class);
        List<Booking> bookings = bookingTypedQuery.getResultList();
        bookingService.approve(bookings.get(0).getId(), true, users.get(0).getId());
        bookingService.approve(bookings.get(1).getId(), true, users.get(0).getId());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.getAllByUserExtended(1000, 0, 10));
        Assertions.assertThrows(ValidationException.class, () -> itemService.getAllByUserExtended(users.get(0).getId(), -1, 10));
        Assertions.assertThrows(ValidationException.class, () -> itemService.getAllByUserExtended(users.get(0).getId(), 0, 0));
        List<ItemDtoExtended> allItems = itemService.getAllByUserExtended(users.get(0).getId(), 0, 10);
        Assertions.assertEquals(allItems.size(), 2);
        Assertions.assertEquals(allItems.get(0).getId(), items.get(0).getId());
        Assertions.assertEquals(allItems.get(1).getId(), items.get(1).getId());
        Assertions.assertEquals(allItems.get(0).getNextBooking().getBookerId(), users.get(1).getId());
        Assertions.assertNull(allItems.get(1).getNextBooking());
        Assertions.assertNull(allItems.get(0).getLastBooking());
        Assertions.assertNull(allItems.get(1).getLastBooking());
    }

    @Test
    void itemServiceGetAvailableByText() {
        User user1 = new User(null, "name1", "user1@user.com");
        User user2 = new User(null, "name2", "user2@user.com");
        userService.add(userMapper.toDto(user1));
        userService.add(userMapper.toDto(user2));
        TypedQuery<User> query = entityManager.createQuery(" from User u order by u.id", User.class);
        List<User> users = query.getResultList();
        Request request1 = new Request(null, "description1", users.get(0), null);
        requestService.add(requestMapper.toInDto(request1), users.get(0).getId());
        TypedQuery<Request> requestQuery = entityManager.createQuery(" from Request r order by r.id", Request.class);
        List<Request> requests = requestQuery.getResultList();
        Item item1 = new Item(null, "name1", "description1", true, users.get(0), null);
        Item item2 = new Item(null, "name2", "description2", true, users.get(0), requests.get(0));
        itemService.add(itemMapper.toDto(item1), users.get(0).getId());
        itemService.add(itemMapper.toDto(item2), users.get(0).getId());
        TypedQuery<Item> itemTypedQuery = entityManager.createQuery(" from Item i order by i.id", Item.class);
        List<Item> items = itemTypedQuery.getResultList();
        Assertions.assertEquals(items.size(), 2);
        List<ItemDto> textItems = itemService.getAvailableByText("dESc", 0, 10);
        Assertions.assertEquals(textItems.size(), 2);
        Assertions.assertThrows(ValidationException.class, () -> itemService.getAvailableByText("qwerty", -1, 10));
        Assertions.assertThrows(ValidationException.class, () -> itemService.getAvailableByText("qwerty", 0, 0));
        textItems = itemService.getAvailableByText("sdec", 0, 10);
        Assertions.assertEquals(textItems.size(), 0);
        textItems = itemService.getAvailableByText("aMe1", 0, 10);
        Assertions.assertEquals(textItems.size(), 1);
    }

    @Test
    void bookingServiceGetAllForItemsTest() {
        User user1 = new User(null, "name1", "user1@user.com");
        User user2 = new User(null, "name2", "user2@user.com");
        userService.add(userMapper.toDto(user1));
        userService.add(userMapper.toDto(user2));
        TypedQuery<User> query = entityManager.createQuery(" from User u order by u.id", User.class);
        List<User> users = query.getResultList();
        Request request1 = new Request(null, "description1", users.get(0), null);
        requestService.add(requestMapper.toInDto(request1), users.get(0).getId());
        TypedQuery<Request> requestQuery = entityManager.createQuery(" from Request r order by r.id", Request.class);
        List<Request> requests = requestQuery.getResultList();
        Item item1 = new Item(null, "name1", "description1", true, users.get(0), null);
        Item item2 = new Item(null, "name2", "description2", true, users.get(0), requests.get(0));
        itemService.add(itemMapper.toDto(item1), users.get(0).getId());
        itemService.add(itemMapper.toDto(item2), users.get(0).getId());
        TypedQuery<Item> itemTypedQuery = entityManager.createQuery(" from Item i order by i.id", Item.class);
        List<Item> items = itemTypedQuery.getResultList();
        Booking booking1 = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), items.get(0), users.get(1), APPROVED);
        Booking booking2 = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), items.get(0), users.get(1), APPROVED);
        bookingService.add(bookingMapper.toInDto(booking1), users.get(1).getId());
        bookingService.add(bookingMapper.toInDto(booking2), users.get(1).getId());
        TypedQuery<Booking> bookingTypedQuery = entityManager.createQuery(" from Booking b order by b.id", Booking.class);
        List<Booking> bookings = bookingTypedQuery.getResultList();
        bookingService.approve(bookings.get(0).getId(), false, users.get(0).getId());
        Assertions.assertThrows(ValidationException.class, () -> bookingService.getAllForItems(users.get(1).getId(), "ALL", -1, 10));
        Assertions.assertThrows(ValidationException.class, () -> bookingService.getAllForItems(users.get(1).getId(), "ALL", 0, 0));
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getAllForItems(1000, "ALL", 0, 0));
        List<BookingDtoExtended> testBookings = bookingService.getAllForItems(users.get(0).getId(), "ALL", 0, 10);
        Assertions.assertEquals(testBookings.size(), 2);
        testBookings = bookingService.getAllForItems(users.get(0).getId(), "FUTURE", 0, 10);
        Assertions.assertEquals(testBookings.size(), 2);
        testBookings = bookingService.getAllForItems(users.get(0).getId(), "CURRENT", 0, 10);
        Assertions.assertEquals(testBookings.size(), 0);
        testBookings = bookingService.getAllForItems(users.get(0).getId(), "PAST", 0, 10);
        Assertions.assertEquals(testBookings.size(), 0);
        testBookings = bookingService.getAllForItems(users.get(0).getId(), "WAITING", 0, 10);
        Assertions.assertEquals(testBookings.size(), 1);
        testBookings = bookingService.getAllForItems(users.get(0).getId(), "REJECTED", 0, 10);
        Assertions.assertEquals(testBookings.size(), 1);
    }

    @Test
    void bookingServiceGetAllByUserIdTest() {
        User user1 = new User(null, "name1", "user1@user.com");
        User user2 = new User(null, "name2", "user2@user.com");
        userService.add(userMapper.toDto(user1));
        userService.add(userMapper.toDto(user2));
        TypedQuery<User> query = entityManager.createQuery(" from User u order by u.id", User.class);
        List<User> users = query.getResultList();
        Request request1 = new Request(null, "description1", users.get(0), null);
        requestService.add(requestMapper.toInDto(request1), users.get(0).getId());
        TypedQuery<Request> requestQuery = entityManager.createQuery(" from Request r order by r.id", Request.class);
        List<Request> requests = requestQuery.getResultList();
        Item item1 = new Item(null, "name1", "description1", true, users.get(0), null);
        Item item2 = new Item(null, "name2", "description2", true, users.get(0), requests.get(0));
        itemService.add(itemMapper.toDto(item1), users.get(0).getId());
        itemService.add(itemMapper.toDto(item2), users.get(0).getId());
        TypedQuery<Item> itemTypedQuery = entityManager.createQuery(" from Item i order by i.id", Item.class);
        List<Item> items = itemTypedQuery.getResultList();
        Booking booking1 = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), items.get(0), users.get(1), APPROVED);
        Booking booking2 = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), items.get(0), users.get(1), APPROVED);
        bookingService.add(bookingMapper.toInDto(booking1), users.get(1).getId());
        bookingService.add(bookingMapper.toInDto(booking2), users.get(1).getId());
        TypedQuery<Booking> bookingTypedQuery = entityManager.createQuery(" from Booking b order by b.id", Booking.class);
        List<Booking> bookings = bookingTypedQuery.getResultList();
        bookingService.approve(bookings.get(0).getId(), false, users.get(0).getId());
        Assertions.assertThrows(ValidationException.class, () -> bookingService.getAllByUserId(users.get(1).getId(), "ALL", -1, 10));
        Assertions.assertThrows(ValidationException.class, () -> bookingService.getAllByUserId(users.get(1).getId(), "ALL", 0, 0));
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getAllByUserId(1000, "ALL", 0, 0));
        List<BookingDtoExtended> testBookings = bookingService.getAllByUserId(users.get(1).getId(), "ALL", 0, 10);
        Assertions.assertEquals(testBookings.size(), 2);
        testBookings = bookingService.getAllForItems(users.get(0).getId(), "FUTURE", 0, 10);
        Assertions.assertEquals(testBookings.size(), 2);
        testBookings = bookingService.getAllForItems(users.get(0).getId(), "CURRENT", 0, 10);
        Assertions.assertEquals(testBookings.size(), 0);
        testBookings = bookingService.getAllForItems(users.get(0).getId(), "PAST", 0, 10);
        Assertions.assertEquals(testBookings.size(), 0);
        testBookings = bookingService.getAllForItems(users.get(0).getId(), "WAITING", 0, 10);
        Assertions.assertEquals(testBookings.size(), 1);
        testBookings = bookingService.getAllForItems(users.get(0).getId(), "REJECTED", 0, 10);
        Assertions.assertEquals(testBookings.size(), 1);
    }
}
