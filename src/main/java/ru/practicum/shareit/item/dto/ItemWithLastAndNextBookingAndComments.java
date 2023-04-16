package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.booking.model.BookingShortForItem;

import java.util.Collection;

@Value
public class ItemWithLastAndNextBookingAndComments {
    Integer id;
    String name;
    String description;
    Boolean available;
    Integer ownerId;
    BookingShortForItem lastBooking;
    BookingShortForItem nextBooking;
    Collection<CommentWithName> comments;
}
