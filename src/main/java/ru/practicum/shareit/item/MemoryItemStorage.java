package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.ForbiddenException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.toItemDto;

@Component
public class MemoryItemStorage implements ItemStorage {
    private int uid;
    private final Map<Integer, Item> items = new HashMap<>();

    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {
        int id = generateId();
        itemDto.setId(id);
        Item item = toItem(itemDto, userId);
        items.put(id, item);
        return itemDto;
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto) {
        Item updatedItem = getItemById(itemId);
        if (updatedItem.getOwner() == userId) {
            if (itemDto.getName() != null) {
                updatedItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                updatedItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                updatedItem.setAvailable(itemDto.getAvailable());
            }
            items.put(itemId, updatedItem);
            ItemDto updatedItemDto = toItemDto(updatedItem);
            return updatedItemDto;
        } else {
            throw new ForbiddenException("Доступ закрыт, нельзя менять item не его владельцу");
        }

    }

    @Override
    public ItemDto getItemDtoById(int itemId) {
        ItemDto itemDto = toItemDto(getItemById(itemId));
        return itemDto;
    }

    private Item getItemById(int itemId) {
        if (items.get(itemId) != null) {
            return items.get(itemId);
        } else {
            throw new NotFoundException("Item not found");
        }
    }

    @Override
    public Collection<ItemDto> getItemsByOwner(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItemsByTest(String text) {
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
