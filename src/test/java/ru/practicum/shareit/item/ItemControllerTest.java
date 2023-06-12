package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemServiceImpl itemService;
    @Autowired
    MockMvc mvc;
    ItemDto itemDto;
    CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1, "Item", "Description", true, null);
        commentDto = new CommentDto(1, "Text", null, 1, LocalDateTime.of(2023, 6, 10, 12, 0, 10));
    }

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(anyInt(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .createItem(1, itemDto);
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyInt(), anyInt(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{id}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .updateItem(1, 1, itemDto);
    }

    @Test
    void getItemDtoById() throws Exception {
        Item item = ItemMapper.toItem(itemDto, new User());
        ItemOutputDto itemOutputDto = ItemMapper.toItemWithBooking(item, null, null);
        when(itemService.getItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemOutputDto);

        mvc.perform(get("/items/{id}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .getItemDtoById(1, 1);
    }

    @Test
    void getItemsByOwner() throws Exception {
        Item item = ItemMapper.toItem(itemDto, new User());
        ItemOutputDto itemOutputDto = ItemMapper.toItemWithBooking(item, null, null);
        when(itemService.getItemsByOwner(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(itemOutputDto));

        mvc.perform(get("/items", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .getItemsByOwner(1, 1, 1);
    }

    @Test
    void searchItemsByText() throws Exception {
        when(itemService.searchItemsByText(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "Item")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .searchItemsByText("Item", 1, 1);
    }

    @Test
    void createComment() throws Exception {
        Comment comment = CommentMapper.toComment(commentDto, new User(1, "User", "user@user.ru"));
        CommentOutputDto commentOutputDto = CommentMapper.toCommentWithName(comment);
        when(itemService.createComment(anyInt(), anyInt(), any(CommentDto.class)))
                .thenReturn(commentOutputDto);

        mvc.perform(post("/items/{id}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Integer.class))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthor().getName())))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.created", is(comment.getCreated().toString())));

        Mockito.verify(itemService, Mockito.times(1))
                .createComment(1, 1, commentDto);
    }
}