package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collection;

@Component
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {
        if (userStorage.getUserById(userId) != null) {
            return itemStorage.createItem(userId, itemDto);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto) {
        return itemStorage.updateItem(userId, itemId, itemDto);
    }

    @Override
    public ItemDto getItemDtoById(int itemId) {
        return itemStorage.getItemDtoById(itemId);
    }

    @Override
    public Collection<ItemDto> getItemsByOwner(int userId) {
        return itemStorage.getItemsByOwner(userId);
    }

    @Override
    public Collection<ItemDto> searchItemsByTest(String text) {
        return itemStorage.searchItemsByTest(text);
    }
}
