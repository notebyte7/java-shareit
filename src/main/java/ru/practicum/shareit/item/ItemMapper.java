package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.BookingShortForItem;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithLastAndNextBookingAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest()
        );
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner
        );
    }

    public static ItemWithLastAndNextBookingAndComments toItemWithBooking(Item item,
                                                                          BookingShortForItem lastBooking, BookingShortForItem nextBooking) {
        return new ItemWithLastAndNextBookingAndComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                lastBooking,
                nextBooking,
                item.getComment().stream()
                        .map(CommentMapper::toCommentWithName)
                        .collect(Collectors.toList())
        );
    }
}
