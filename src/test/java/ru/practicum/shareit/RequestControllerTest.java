package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
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
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = RequestController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestControllerTest {
    private final RequestMapper requestMapper = new RequestMapper();
    @MockBean
    RequestService requestService;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final RequestController requestController;
    MockMvc mockMvc;

    @BeforeAll
    static void setDate() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(requestController).build();
    }

    @Test
    void addTest() throws Exception {
        when(requestService.add(any(RequestDto.class), anyInt())).thenAnswer(invocation -> {
            RequestDto argument = invocation.getArgument(0, RequestDto.class);
            return requestMapper.toInDto(new Request(1, argument.getDescription(), new User(argument.getRequestorId(), "name1", "user1@user.com"), argument.getCreated()));
        });
        RequestDto requestDto1 = requestMapper.toInDto(new Request(1, "description1", new User(1, "name1", "user1@user.com"), LocalDateTime.now()));
        mockMvc.perform(post("/requests").content(objectMapper.writeValueAsString(requestDto1)).characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto1.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(requestDto1.getDescription()), String.class));
        requestDto1.setDescription(null);
        mockMvc.perform(post("/requests").content(objectMapper.writeValueAsString(requestDto1)).characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByIdTest() throws Exception {
        when(requestService.getById(anyInt(), anyInt())).thenAnswer(invocation -> {
            Integer requestId = invocation.getArgument(0, Integer.class);
            Integer userId = invocation.getArgument(1, Integer.class);
            return requestMapper.toOutDto(new Request(requestId, "description1", new User(userId, "name1", "user1@user.com"), LocalDateTime.now()), new ArrayList<>());
        });
        RequestDto requestDto1 = requestMapper.toInDto(new Request(1, "description1", new User(1, "name1", "user1@user.com"), LocalDateTime.now()));
        mockMvc.perform(get("/requests/{requestId}", 1).content(objectMapper.writeValueAsString(requestDto1)).characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto1.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(requestDto1.getDescription()), String.class));

    }

    @Test
    void getAllByUserIdTest() throws Exception {
        when(requestService.getAllByUserId(anyInt())).thenAnswer(invocation -> {
            Integer userId = invocation.getArgument(0, Integer.class);
            return List.of(requestMapper.toOutDto(new Request(1, "description1", new User(userId, "name1", "user1@user.com"), LocalDateTime.now()), new ArrayList<>()));
        });
        RequestDto requestDto1 = requestMapper.toInDto(new Request(1, "description1", new User(1, "name1", "user1@user.com"), LocalDateTime.now()));
        mockMvc.perform(get("/requests").characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDto1.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(requestDto1.getDescription()), String.class));
    }

    @Test
    void getAllTest() throws Exception {
        when(requestService.getAll(anyInt(), anyInt(), anyInt())).thenAnswer(invocation -> {
            Integer userId = invocation.getArgument(0, Integer.class);
            return List.of(requestMapper.toOutDto(new Request(1, "description1", new User(userId, "name1", "user1@user.com"), LocalDateTime.now()), new ArrayList<>()));
        });
        RequestDto requestDto1 = requestMapper.toInDto(new Request(1, "description1", new User(1, "name1", "user1@user.com"), LocalDateTime.now()));
        mockMvc.perform(get("/requests/all")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)).characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDto1.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(requestDto1.getDescription()), String.class));
    }
}
