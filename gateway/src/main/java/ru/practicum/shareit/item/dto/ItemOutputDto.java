package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.Collection;

@Value
public class ItemOutputDto {
    Integer id;
    String name;
    String description;
    Boolean available;
    Integer ownerId;
    BookingShortDto lastBooking;
    BookingShortDto nextBooking;
    Collection<CommentOutputDto> comments;
}
