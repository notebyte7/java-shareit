package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(int userId, ItemDto itemDto);

    ItemDto updateItem(int userId, int itemId, ItemDto itemDto);

    ItemDto getItemDtoById(int itemId);

    Collection<ItemDto> getItemsByOwner(int userId);

    Collection<ItemDto> searchItemsByTest(String text);
}
