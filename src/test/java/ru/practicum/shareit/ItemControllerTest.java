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
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {
    private final ItemMapper itemMapper = new ItemMapper();
    private final CommentMapper commentMapper = new CommentMapper();
    @MockBean
    ItemService itemService;
    @MockBean
    CommentService commentService;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final ItemController itemController;
    MockMvc mockMvc;

    @BeforeAll
    static void setDate() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    void addTest() throws Exception {
        when(itemService.add(any(ItemDto.class), anyInt())).thenAnswer(invocation -> {
            ItemDto argument = invocation.getArgument(0, ItemDto.class);
            Integer userId = invocation.getArgument(1, Integer.class);
            return itemMapper.toDto(new Item(1, argument.getName(), argument.getDescription(), argument.getAvailable(), new User(userId, "uname1", "user1@user.com"), null));
        });
        ItemDto itemDto = itemMapper.toDto(new Item(1, "name1", "description1", true, new User(1, "uname1", "user1@user.com"), null));
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto)).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Integer.class));
    }

    @Test
    void updateTest() throws Exception {
        when(itemService.update(any(ItemDto.class), anyInt(), anyInt())).thenAnswer(invocation -> {
            ItemDto argument = invocation.getArgument(0, ItemDto.class);
            Integer userId = invocation.getArgument(1, Integer.class);
            Integer itemId = invocation.getArgument(2, Integer.class);
            return itemMapper.toDto(new Item(itemId, argument.getName(), argument.getDescription(), argument.getAvailable(), new User(userId, "uname1", "user1@user.com"), null));
        });
        ItemDto itemDto = itemMapper.toDto(new Item(1, "name1", "description1", true, new User(1, "uname1", "user1@user.com"), null));
        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto)).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Integer.class));
    }

    @Test
    void getTest() throws Exception {
        when(itemService.get(anyInt(), anyInt())).thenAnswer(invocation -> {
            Integer userId = invocation.getArgument(1, Integer.class);
            Integer itemId = invocation.getArgument(0, Integer.class);
            return itemMapper.toDtoExtended(new Item(itemId, "name1", "description1", true, new User(userId, "uname1", "user1@user.com"), null), null);
        });
        ItemDto itemDto = itemMapper.toDto(new Item(1, "name1", "description1", true, new User(1, "uname1", "user1@user.com"), null));
        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Integer.class));
    }

    @Test
    void getAllByUserId() throws Exception {
        when(itemService.getAllByUserExtended(anyInt(), anyInt(), anyInt())).thenAnswer(invocation -> {
            Integer userId = invocation.getArgument(0, Integer.class);
            return List.of(
                    itemMapper.toDtoExtended(new Item(1, "name1", "description1", true, new User(userId, "uname1", "user1@user.com"), null), null)
            );
        });
        ItemDto itemDto = itemMapper.toDto(new Item(1, "name1", "description1", true, new User(1, "uname1", "user1@user.com"), null));
        mockMvc.perform(get("/items")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].ownerId", is(itemDto.getOwnerId()), Integer.class));
    }

    @Test
    void getAvailableByTextTest() throws Exception {
        when(itemService.getAvailableByText(anyString(), anyInt(), anyInt())).thenAnswer(invocation -> List.of(
                itemMapper.toDtoExtended(new Item(1, "name1", "description1", true, new User(1, "uname1", "user1@user.com"), null), null)
        ));
        ItemDto itemDto = itemMapper.toDto(new Item(1, "name1", "description1", true, new User(1, "uname1", "user1@user.com"), null));
        mockMvc.perform(get("/items/search")
                        .param("text", "desc")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].ownerId", is(itemDto.getOwnerId()), Integer.class));
    }

    @Test
    void addCommentTest() throws Exception {
        when(commentService.add(any(CommentDto.class), anyInt(), anyInt()))
                .thenAnswer(invocation -> {
                    CommentDto argument = invocation.getArgument(0, CommentDto.class);
                    Integer itemId = invocation.getArgument(1, Integer.class);
                    Integer userId = invocation.getArgument(2, Integer.class);
                    return commentMapper.toDto(new Comment(1,
                            argument.getText(),
                            new Item(itemId, "name1", "description1", true,
                                    new User(1, "uname1", "user1@user.com"), null),
                            new User(userId, "uname1", "user@user@com"),
                            argument.getCreated()));
                });
        CommentDto commentDto = new CommentDto(1, "text1", 1, 1, "uname1", LocalDateTime.now());
        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.itemId", is(commentDto.getItemId()), Integer.class))
                .andExpect(jsonPath("$.authorId", is(commentDto.getAuthorId()), Integer.class));
    }
}
