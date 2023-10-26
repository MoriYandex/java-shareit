package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CommentDto;

public interface CommentService {
    CommentDto add(CommentDto commentDto, Integer itemId, Integer userId);
}
