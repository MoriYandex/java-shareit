package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
import java.time.ZoneId;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests extends ControllerTests<ItemController> {
    @Autowired
    public ItemControllerTests(ItemController testController) {
        super(testController);
    }

    private final ItemMapper itemMapper = new ItemMapper();
    private final CommentMapper commentMapper = new CommentMapper();
    @MockBean
    ItemService itemService;
    @MockBean
    CommentService commentService;

    @Test
    void addTest() throws Exception {
        when(itemService.add(any(ItemDto.class), anyLong())).thenAnswer(invocation -> {
            ItemDto argument = invocation.getArgument(0, ItemDto.class);
            Long userId = invocation.getArgument(1, Long.class);
            return itemMapper.toDto(new Item(1L, argument.getName(), argument.getDescription(), argument.getAvailable(), new User(userId, "uname1", "user1@user.com"), null));
        });
        ItemDto itemDto = itemMapper.toDto(new Item(1L, "name1", "description1", true, new User(1L, "uname1", "user1@user.com"), null));
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto)).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class));
    }

    @Test
    void updateTest() throws Exception {
        when(itemService.update(any(ItemDto.class), anyLong(), anyLong())).thenAnswer(invocation -> {
            ItemDto argument = invocation.getArgument(0, ItemDto.class);
            Long userId = invocation.getArgument(1, Long.class);
            Long itemId = invocation.getArgument(2, Long.class);
            return itemMapper.toDto(new Item(itemId, argument.getName(), argument.getDescription(), argument.getAvailable(), new User(userId, "uname1", "user1@user.com"), null));
        });
        ItemDto itemDto = itemMapper.toDto(new Item(1L, "name1", "description1", true, new User(1L, "uname1", "user1@user.com"), null));
        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto)).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class));
    }

    @Test
    void getTest() throws Exception {
        when(itemService.get(anyLong(), anyLong())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(1, Long.class);
            Long itemId = invocation.getArgument(0, Long.class);
            return itemMapper.toDtoExtended(new Item(itemId, "name1", "description1", true, new User(userId, "uname1", "user1@user.com"), null), null);
        });
        ItemDto itemDto = itemMapper.toDto(new Item(1L, "name1", "description1", true, new User(1L, "uname1", "user1@user.com"), null));
        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class));
    }

    @Test
    void getAllByUserId() throws Exception {
        when(itemService.getAllByUserExtended(anyLong(), anyInt(), anyInt())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0, Long.class);
            return List.of(
                    itemMapper.toDtoExtended(new Item(1L, "name1", "description1", true, new User(userId, "uname1", "user1@user.com"), null), null)
            );
        });
        ItemDto itemDto = itemMapper.toDto(new Item(1L, "name1", "description1", true, new User(1L, "uname1", "user1@user.com"), null));
        mockMvc.perform(get("/items")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].ownerId", is(itemDto.getOwnerId()), Long.class));
    }

    @Test
    void getAvailableByTextTest() throws Exception {
        when(itemService.getAvailableByText(anyString(), anyInt(), anyInt())).thenAnswer(invocation -> List.of(
                itemMapper.toDtoExtended(new Item(1L, "name1", "description1", true, new User(1L, "uname1", "user1@user.com"), null), null)
        ));
        ItemDto itemDto = itemMapper.toDto(new Item(1L, "name1", "description1", true, new User(1L, "uname1", "user1@user.com"), null));
        mockMvc.perform(get("/items/search")
                        .param("text", "desc")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].ownerId", is(itemDto.getOwnerId()), Long.class));
    }

    @Test
    void addCommentTest() throws Exception {
        when(commentService.add(any(CommentDto.class), anyLong(), anyLong()))
                .thenAnswer(invocation -> {
                    CommentDto argument = invocation.getArgument(0, CommentDto.class);
                    Long itemId = invocation.getArgument(1, Long.class);
                    Long userId = invocation.getArgument(2, Long.class);
                    return commentMapper.toDto(new Comment(1L,
                            argument.getText(),
                            new Item(itemId, "name1", "description1", true,
                                    new User(1L, "uname1", "user1@user.com"), null),
                            new User(userId, "uname1", "user@user@com"),
                            argument.getCreated()));
                });
        CommentDto commentDto = new CommentDto(1L, "text1", 1L, 1L, "uname1", LocalDateTime.now(ZoneId.of("Europe/Moscow")));
        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.itemId", is(commentDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.authorId", is(commentDto.getAuthorId()), Long.class));
    }
}
