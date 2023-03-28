package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MemoryItemStorage implements ItemStorage {
    private int uid;
    private final Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        int id = generateId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item updateItem(int userId, int itemId, Item item) {
        Item updatedItem = getItemById(itemId);
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, updatedItem);
        return updatedItem;

    }

    public Item getItemById(int itemId) {
        return items.get(itemId);

    }

    @Override
    public Collection<Item> getItemsByOwner(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> searchItemsByText(String text) {
        if (text.length() > 0) {
            String textLowerCase = text.toLowerCase();
            return items.values().stream()
                    .filter(item -> item.getName().toLowerCase().contains(textLowerCase) ||
                            item.getDescription().toLowerCase().contains(textLowerCase))
                    .filter(item -> item.getAvailable().equals(true))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private int generateId() {
        return ++uid;
    }
}
