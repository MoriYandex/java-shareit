package ru.practicum.shareit.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.users.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping()
    public ResponseEntity<Object> add(@RequestBody @Valid UserDto userDto) {
        return userClient.addUser(userDto);
    }

    @PatchMapping(path = "/{userId}")
    public ResponseEntity<Object> update(@Positive @PathVariable(name = "userId") long userId,
                                         @RequestBody UserDto userDto) {
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<Object> get(@Positive @PathVariable(name = "userId") long userId) {
        return userClient.get(userId);
    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<Object> delete(@Positive @PathVariable(name = "userId") long userId) {
        return userClient.delete(userId);
    }
}
