package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.ForbiddenException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.toItemDto;

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
            Item item = toItem(itemDto, userId);
            return toItemDto(itemStorage.createItem(item));
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto) {
        if (itemStorage.getItemById(itemId).getOwner() == userId) {
            Item item = toItem(itemDto, userId);
            return toItemDto(itemStorage.updateItem(userId, itemId, item));
        } else {
            throw new ForbiddenException("Доступ закрыт, нельзя менять item не его владельцу");
        }
    }

    @Override
    public ItemDto getItemDtoById(int itemId) {
        if (itemStorage.getItemById(itemId) != null) {
            return toItemDto(itemStorage.getItemById(itemId));
        } else {
            throw new NotFoundException("Item not found");
        }
    }

    @Override
    public Collection<ItemDto> getItemsByOwner(int userId) {
        return itemStorage.getItemsByOwner(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItemsByTest(String text) {
        return itemStorage.searchItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
