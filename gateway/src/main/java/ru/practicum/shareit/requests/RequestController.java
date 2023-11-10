package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping()
    public ResponseEntity<Object> add(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestBody @Valid RequestDto requestDto) {
        return requestClient.add(userId, requestDto);
    }

    @GetMapping(path = "/{requestId}")
    public ResponseEntity<Object> getById(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                          @Positive @PathVariable(name = "requestId") long requestId) {
        return requestClient.getById(userId, requestId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllByUserId(@Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestClient.getAllByUserId(userId);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Object> getAll(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                         @PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
                                         @Positive @RequestParam(name = "size", required = false) Integer size) {
        return requestClient.getAll(userId, from, size);
    }
}
