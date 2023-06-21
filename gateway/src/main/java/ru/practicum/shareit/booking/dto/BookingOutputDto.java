package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Value
public class BookingOutputDto {
    Integer id;
    LocalDateTime start;
    LocalDateTime end;
    ItemDto item;
    UserDto booker;
    StatusDto status;
}
