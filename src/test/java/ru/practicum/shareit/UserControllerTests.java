package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTests {
    private final UserMapper userMapper = new UserMapper();
    @MockBean
    UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserController userController;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void addTest() throws Exception {
        when(userService.add(any(UserDto.class))).thenAnswer(invocation -> {
            UserDto argument = invocation.getArgument(0, UserDto.class);
            return userMapper.toDto(new User(1, argument.getName(), argument.getEmail()));
        });
        UserDto userDto = userMapper.toDto(new User(1, "name1", "user1@user.com"));
        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(userDto)).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
        userDto = userMapper.toDto(new User(1, "name1", ""));
        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(userDto)).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTest() throws Exception {
        when(userService.update(any(UserDto.class), anyInt())).thenAnswer(invocation -> {
            UserDto argument = invocation.getArgument(0, UserDto.class);
            return userMapper.toDto(new User(argument.getId(), argument.getName(), argument.getEmail()));
        });
        UserDto userDto = userMapper.toDto(new User(1, "name1", "user1@user.com"));
        mockMvc.perform(patch("/users/{userId}", 1).content(objectMapper.writeValueAsString(userDto)).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));

    }

    @Test
    void getTest() throws Exception {
        when(userService.get(anyInt())).thenAnswer(invocation -> {
            Integer argument = invocation.getArgument(0, Integer.class);
            return userMapper.toDto(new User(argument, "name1", "user1@user.com"));
        });
        UserDto userDto = userMapper.toDto(new User(1, "name1", "user1@user.com"));
        mockMvc.perform(get("/users/{userId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));

    }

    @Test
    void getAllTest() throws Exception {
        when(userService.getAll()).thenAnswer(invocation -> List.of(new User(1, "name1", "user1@user.com"), new User(2, "name2", "user2@user.com")));
        UserDto userDto1 = userMapper.toDto(new User(1, "name1", "user1@user.com"));
        UserDto userDto2 = userMapper.toDto(new User(2, "name2", "user2@user.com"));
        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto1.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(userDto1.getName()), String.class))
                .andExpect(jsonPath("$[0].email", is(userDto1.getEmail()), String.class))
                .andExpect(jsonPath("$[1].id", is(userDto2.getId()), Integer.class))
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
