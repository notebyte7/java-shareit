package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemMapper.toItemDto;

@Component
public class MemoryItemStorage implements ItemStorage {
    private int uid;
    private final Map<Integer, Item> items = new HashMap<>();

    @Override
    public ItemDto createItem(Item item) {
        int id = generateId();
        item.setId(id);
        items.put(id, item);
        return toItemDto(item);
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, Item item) {
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
        return toItemDto(updatedItem);

    }

    @Override
    public ItemDto getItemDtoById(int itemId) {
        return toItemDto(getItemById(itemId));
    }

    public Item getItemById(int itemId) {
        return items.get(itemId);

    }

    @Override
    public Collection<ItemDto> getItemsByOwner(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItemsByText(String text) {
        if (text.length() > 0) {
            String textLowerCase = text.toLowerCase();
            return items.values().stream()
                    .filter(item -> item.getName().toLowerCase().contains(textLowerCase) ||
                            item.getDescription().toLowerCase().contains(textLowerCase))
                    .filter(item -> item.getAvailable().equals(true))
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private int generateId() {
        return ++uid;
    }
}
