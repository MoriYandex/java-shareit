package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExtended;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping()
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(itemDto, userId);
    }

    //На update автоматическая валидация работать не будет, так как можно передавать неполные данные!
    @PatchMapping(path = "/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable(name = "itemId") Integer itemId, @RequestBody ItemDto itemDto) {
        return itemService.update(itemDto, userId, itemId);
    }

    @GetMapping(path = "/{itemId}")
    public ItemDtoExtended get(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable(name = "itemId") Integer itemId) {
        return itemService.get(itemId, userId);
    }

    @GetMapping()
    public List<ItemDtoExtended> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAllByUserExtended(userId);
    }

    @GetMapping(path = "/search")
    public List<ItemDto> getAvailableByText(@RequestParam(name = "text") String text) {
        return itemService.getAvailableByText(text);
    }

    @PostMapping(path = "/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable(name = "itemId") Integer itemId, @RequestBody @Valid CommentDto commentDto) {
        return commentService.add(commentDto, itemId, userId);
    }
}
