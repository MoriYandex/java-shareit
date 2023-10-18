package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    //На update автоматическая валидация работать не будет, так как можно передавать неполные данные!
    @PatchMapping(path = "/{userId}")
    public UserDto update(@PathVariable(name = "userId") Integer userId, @RequestBody UserDto userDto) {
        return userService.update(userDto, userId);
    }

    @GetMapping()
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping(path = "/{userId}")
    public UserDto get(@PathVariable(name = "userId") Integer userID) {
        return userService.get(userID);
    }

    @DeleteMapping(path = "/{userId}")
    public void delete(@PathVariable(name = "userId") Integer userId) {
        userService.delete(userId);
    }
}
