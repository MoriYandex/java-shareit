package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private static final String AUTHOR_NOT_FOUND_MESSAGE = "Не найден создатель отзыва!";
    private static final String BOOKING_NOT_FOUND_MESSAGE = "Не найдено подтверждение использование вещи пользователем!";
    private static final String ITEM_NOT_FOUND_MESSAGE = "Не найдена вещь для отзыва!";

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto add(CommentDto commentDto, Integer itemId, Integer userId) {
        log.info("Создание отзыва пользователем {} для вещи {}", userId, itemId);
        User author = userRepository.findById(userId).orElse(null);
        if (author == null) {
            log.error(AUTHOR_NOT_FOUND_MESSAGE);
            throw new NotFoundException(AUTHOR_NOT_FOUND_MESSAGE);
        }
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            log.error(ITEM_NOT_FOUND_MESSAGE);
            throw new NotFoundException(ITEM_NOT_FOUND_MESSAGE);
        }
        List<Booking> bookingList = bookingRepository.findAllByItemAndBookerAndStatusAndEndIsBefore(item, author, BookingStatus.APPROVED, LocalDateTime.now());
        if (bookingList.isEmpty()) {
            log.error(BOOKING_NOT_FOUND_MESSAGE);
            throw new ValidationException(BOOKING_NOT_FOUND_MESSAGE);
        }
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentMapper.fromDto(commentDto, item, author);
        return commentMapper.toDto(commentRepository.save(comment));
    }
}
