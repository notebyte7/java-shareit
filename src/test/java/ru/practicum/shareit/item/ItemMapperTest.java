package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemMapperTest {

    @Test
    void toItemDto() {
        User user = new User(1, "User", "user@user.ru");
        ItemRequest itemRequest = new ItemRequest(1, "Description", user, LocalDateTime.now(), List.of());
        Item item = new Item(1, "Item", "Description", true, user, itemRequest, List.of());
        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());
    }

    @Test
    void toItem() {
        User user = new User(1, "User", "user@user.ru");
        ItemRequest itemRequest = new ItemRequest(1, "Description", user, LocalDateTime.now(), List.of());
        ItemDto itemDto = new ItemDto(1, "Item", "Description", true, itemRequest.getId());
        Item item = ItemMapper.toItem(itemDto, user, itemRequest);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());
    }

    @Test
    void toItemWithBooking() {
        User user = new User(1, "User", "user@user.ru");
        ItemRequest itemRequest = new ItemRequest(1, "Description", user, LocalDateTime.now(), List.of());
        Item item = new Item(1, "Item", "Description", true, user, itemRequest, List.of());
        BookingShortDto lastBooking = new BookingShortDto(1, 1);
        BookingShortDto nextBooking = new BookingShortDto(2, 2);
        ItemOutputDto itemOutputDto = ItemMapper.toItemWithBooking(item, lastBooking, nextBooking);

        assertEquals(item.getId(), itemOutputDto.getId());
        assertEquals(item.getName(), itemOutputDto.getName());
        assertEquals(item.getDescription(), itemOutputDto.getDescription());
        assertEquals(item.getAvailable(), itemOutputDto.getAvailable());
        assertEquals(lastBooking, itemOutputDto.getLastBooking());
        assertEquals(nextBooking, itemOutputDto.getNextBooking());
    }
}