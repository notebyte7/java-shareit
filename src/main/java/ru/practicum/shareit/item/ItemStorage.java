package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    ItemDto createItem(Item item);

    ItemDto updateItem(int userId, int itemId, Item item);

    Collection<ItemDto> getItemsByOwner(int userId);

    Collection<ItemDto> searchItemsByText(String text);

    ItemDto getItemDtoById(int itemId);

    Item getItemById(int itemId);
}
