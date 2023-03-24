package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    private Integer id;
    @NotBlank(message = "Поле name не должно быть пустым")
    private final String name;
    @NotBlank(message = "Поле description не должно быть пустым")
    private final String description;
    @NotNull
    private final Boolean available;
    private final Integer requestId;

    public ItemDto(Integer id, String name, String description, Boolean available, Integer requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
