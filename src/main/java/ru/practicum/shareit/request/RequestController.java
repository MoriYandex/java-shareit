package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping()
    public RequestDto add(@RequestBody RequestDto requestDto) {
        return requestService.add(requestDto);
    }

    @PatchMapping(path = "/{requestId}")
    public RequestDto update(@PathVariable(name = "requestId") Integer requestId, @RequestBody RequestDto requestDto) {
        return requestService.update(requestDto, requestId);
    }

    @GetMapping(path = "/{requestId}")
    public RequestDto get(@PathVariable(name = "requestId") Integer requestId) {
        return requestService.get(requestId);
    }

    @DeleteMapping(path = "/{requestId}")
    public void delete(@PathVariable(name = "requestId") Integer requestId) {
        requestService.delete(requestId);
    }
}
