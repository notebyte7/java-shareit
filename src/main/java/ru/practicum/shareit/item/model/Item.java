package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private final Integer id;
    @NotBlank(message = "Поле name не должно быть пустым")
    private final String name;
    @NotBlank(message = "Поле description не должно быть пустым")
    private final String description;
    private final Boolean available;
    @NotBlank(message = "Поле owner не должно быть пустым")
    private final User owner;
    private final ItemRequest request;

}
