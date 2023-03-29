package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item createItem(Item item);

    Item updateItem(int userId, int itemId, Item item);

    Collection<Item> getItemsByOwner(int userId);

    Collection<Item> searchItemsByText(String text);

    Item getItemById(int itemId);
}
