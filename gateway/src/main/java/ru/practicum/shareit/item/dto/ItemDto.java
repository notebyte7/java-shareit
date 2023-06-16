package ru.practicum.shareit.item.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Value
public class ItemDto {
    Integer id;
    @NotBlank(message = "Поле name не должно быть пустым")
    String name;
    @NotBlank(message = "Поле description не должно быть пустым")
    String description;
    @NotNull
    Boolean available;
    Integer requestId;
}
