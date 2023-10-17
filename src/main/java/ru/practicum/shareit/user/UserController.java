package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public User add(@Valid @RequestBody User user) {
        return userService.add(user);
    }

    @PatchMapping(path = "/{userId}")
    public User update(@PathVariable(name = "userId") Integer userId, @Valid @RequestBody User user) {
        return userService.update(user, userId);
    }

    @GetMapping()
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping(path = "/{userId}")
    public User get(@PathVariable(name = "userId") Integer userID) {
        return userService.get(userID);
    }

    @DeleteMapping(path = "/{userId}")
    public void delete(@PathVariable(name = "userId") Integer userId) {
        userService.delete(userId);
    }
}
