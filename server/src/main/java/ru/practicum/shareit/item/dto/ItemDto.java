package ru.practicum.shareit.item.dto;

import lombok.Value;

/**
 * TODO Sprint add-controllers.
 */
@Value
public class ItemDto {
    Integer id;
    String name;
    String description;
    Boolean available;
    Integer requestId;
}
