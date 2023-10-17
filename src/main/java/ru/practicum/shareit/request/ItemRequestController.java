package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping()
    public ItemRequestDto add(@RequestBody ItemRequestDto requestDto) {
        return requestService.add(requestDto);
    }

    @PatchMapping(path = "/{requestId}")
    public ItemRequestDto update(@PathVariable(name = "requestId") Integer requestId, @RequestBody ItemRequestDto requestDto) {
        return requestService.update(requestDto, requestId);
    }

    @GetMapping(path = "/{requestId}")
    public ItemRequestDto get(@PathVariable(name = "requestId") Integer requestId) {
        return requestService.get(requestId);
    }

    @DeleteMapping(path = "/{requestId}")
    public void delete(@PathVariable(name = "requestId") Integer requestId) {
        requestService.delete(requestId);
    }
}
