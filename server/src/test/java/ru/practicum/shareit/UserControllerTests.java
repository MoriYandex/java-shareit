package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests extends ControllerTests<UserController> {
    @Autowired
    public UserControllerTests(UserController testController) {
        super(testController);
    }

    private final UserMapper userMapper = new UserMapper();
    @MockBean
    UserService userService;

    @Test
    void addTest() throws Exception {
        when(userService.add(any(UserDto.class))).thenAnswer(invocation -> {
            UserDto argument = invocation.getArgument(0, UserDto.class);
            return userMapper.toDto(new User(1L, argument.getName(), argument.getEmail()));
        });
        UserDto userDto = userMapper.toDto(new User(1L, "name1", "user1@user.com"));
        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(userDto)).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
    }

    @Test
    void updateTest() throws Exception {
        when(userService.update(any(UserDto.class), anyLong())).thenAnswer(invocation -> {
            UserDto argument = invocation.getArgument(0, UserDto.class);
            return userMapper.toDto(new User(argument.getId(), argument.getName(), argument.getEmail()));
        });
        UserDto userDto = userMapper.toDto(new User(1L, "name1", "user1@user.com"));
        mockMvc.perform(patch("/users/{userId}", 1L).content(objectMapper.writeValueAsString(userDto)).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));

    }

    @Test
    void getTest() throws Exception {
        when(userService.get(anyLong())).thenAnswer(invocation -> {
            Long argument = invocation.getArgument(0, Long.class);
            return userMapper.toDto(new User(argument, "name1", "user1@user.com"));
        });
        UserDto userDto = userMapper.toDto(new User(1L, "name1", "user1@user.com"));
        mockMvc.perform(get("/users/{userId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));

    }

    @Test
    void getAllTest() throws Exception {
        when(userService.getAll()).thenAnswer(invocation -> List.of(new User(1L, "name1", "user1@user.com"),
                new User(2L, "name2", "user2@user.com")));
        UserDto userDto1 = userMapper.toDto(new User(1L, "name1", "user1@user.com"));
        UserDto userDto2 = userMapper.toDto(new User(2L, "name2", "user2@user.com"));
        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto1.getName()), String.class))
                .andExpect(jsonPath("$[0].email", is(userDto1.getEmail()), String.class))
                .andExpect(jsonPath("$[1].id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userDto2.getName()), String.class))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail()), String.class));

    }

    @Test
    void deleteTest() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("delete"));
    }
}
