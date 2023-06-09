package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private final int id;
    @NotBlank
    private final String description;
    private final UserDto requestor;
    private final LocalDateTime created;
    private final Collection<ItemDto> items;
}
