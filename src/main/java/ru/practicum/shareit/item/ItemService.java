package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(int userId, ItemDto itemDto);

    ItemDto updateItem(int userId, int itemId, ItemDto itemDto);


    ItemOutputDto getItemDtoById(int userId, int itemId);

    Collection<ItemOutputDto> getItemsByOwner(int userId);

    Collection<ItemDto> searchItemsByTest(String text);

    CommentOutputDto createComment(int userId, int itemId, CommentDto comment);
}
