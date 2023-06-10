package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;

import java.util.Collection;

@Service
public interface ItemService {
    ItemDto createItem(int userId, ItemDto itemDto);

    ItemDto updateItem(int userId, int itemId, ItemDto itemDto);


    ItemOutputDto getItemDtoById(int userId, int itemId);

    Collection<ItemOutputDto> getItemsByOwner(int userId, Integer from, Integer size);

    Collection<ItemDto> searchItemsByText(String text, Integer from, Integer size);

    CommentOutputDto createComment(int userId, int itemId, CommentDto comment);
}
