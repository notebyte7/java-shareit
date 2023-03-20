package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    @NotBlank(message = "Поле name не должно быть пустым")
    private final String name;
    @NotBlank(message = "Поле description не должно быть пустым")
    private final String description;
    @NonNull
    private final Boolean available;
    private final Integer requestId;
}
