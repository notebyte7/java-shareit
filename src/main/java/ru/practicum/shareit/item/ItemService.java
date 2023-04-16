package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentWithName;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithLastAndNextBookingAndComments;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(int userId, ItemDto itemDto);

    ItemDto updateItem(int userId, int itemId, ItemDto itemDto);


    ItemWithLastAndNextBookingAndComments getItemDtoById(int userId, int itemId);

    Collection<ItemWithLastAndNextBookingAndComments> getItemsByOwner(int userId);

    Collection<ItemDto> searchItemsByTest(String text);

    CommentWithName createComment(int userId, int itemId, CommentDto comment);
}
