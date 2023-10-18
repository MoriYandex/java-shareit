package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping()
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(itemDto, userId);
    }

    //На update автоматическая валидация работать не будет, так как можно передавать неполные данные!
    @PatchMapping(path = "/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable(name = "itemId") Integer itemId, @RequestBody ItemDto itemDto) {
        return itemService.update(itemDto, userId, itemId);
    }

    @GetMapping(path = "{itemId}")
    public ItemDto get(@PathVariable(name = "itemId") Integer itemId) {
        return itemService.get(itemId);
    }

    @GetMapping()
    public List<ItemDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAllByUserId(userId);
    }

    @GetMapping(path = "/search")
    public List<ItemDto> getAvailableByText(@RequestParam(name = "text") String text) {
        return itemService.getAvailableByText(text);
    }
}
