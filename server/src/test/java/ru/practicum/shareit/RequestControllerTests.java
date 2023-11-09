package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTests extends ControllerTests<RequestController> {
    @Autowired
    public RequestControllerTests(RequestController testController) {
        super(testController);
    }

    private final RequestMapper requestMapper = new RequestMapper();
    @MockBean
    RequestService requestService;

    @Test
    void addTest() throws Exception {
        when(requestService.add(any(RequestDto.class), anyLong())).thenAnswer(invocation -> {
            RequestDto argument = invocation.getArgument(0, RequestDto.class);
            return requestMapper.toInDto(new Request(1L, argument.getDescription(), new User(argument.getRequestorId(), "name1", "user1@user.com"), argument.getCreated()));
        });
        RequestDto requestDto1 = requestMapper.toInDto(new Request(1L, "description1", new User(1L, "name1", "user1@user.com"), LocalDateTime.now()));
        mockMvc.perform(post("/requests").content(objectMapper.writeValueAsString(requestDto1)).characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto1.getDescription()), String.class));
    }

    @Test
    void getByIdTest() throws Exception {
        when(requestService.getById(anyLong(), anyLong())).thenAnswer(invocation -> {
            Long requestId = invocation.getArgument(0, Long.class);
            Long userId = invocation.getArgument(1, Long.class);
            return requestMapper.toOutDto(new Request(requestId, "description1", new User(userId, "name1", "user1@user.com"), LocalDateTime.now()), new ArrayList<>());
        });
        RequestDto requestDto1 = requestMapper.toInDto(new Request(1L, "description1", new User(1L, "name1", "user1@user.com"), LocalDateTime.now()));
        mockMvc.perform(get("/requests/{requestId}", 1).content(objectMapper.writeValueAsString(requestDto1)).characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto1.getDescription()), String.class));

    }

    @Test
    void getAllByUserIdTest() throws Exception {
        when(requestService.getAllByUserId(anyLong())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0, Long.class);
            return List.of(requestMapper.toOutDto(new Request(1L, "description1", new User(userId, "name1", "user1@user.com"), LocalDateTime.now()), new ArrayList<>()));
        });
        RequestDto requestDto1 = requestMapper.toInDto(new Request(1L, "description1", new User(1L, "name1", "user1@user.com"), LocalDateTime.now()));
        mockMvc.perform(get("/requests").characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto1.getDescription()), String.class));
    }

    @Test
    void getAllTest() throws Exception {
        when(requestService.getAll(anyLong(), anyInt(), anyInt())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0, Long.class);
            return List.of(requestMapper.toOutDto(new Request(1L, "description1", new User(userId, "name1", "user1@user.com"), LocalDateTime.now()), new ArrayList<>()));
        });
        RequestDto requestDto1 = requestMapper.toInDto(new Request(1L, "description1", new User(1L, "name1", "user1@user.com"), LocalDateTime.now()));
        mockMvc.perform(get("/requests/all")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)).characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto1.getDescription()), String.class));
    }
}
