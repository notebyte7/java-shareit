package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Value
public class BookingOutputDto {
    Integer id;
    LocalDateTime start;
    LocalDateTime end;
    Item item;
    User booker;
    StatusDto status;
}
