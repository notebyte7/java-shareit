package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private final int id;
    private final String description;
    private final User requestor;
    private final LocalDateTime created;
    private final Collection<ItemDto> items;
}
